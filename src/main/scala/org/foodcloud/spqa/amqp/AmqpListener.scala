package org.foodcloud.spqa.amqp

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Supervision.{Decider, Resume}
import akka.stream.alpakka.amqp._
import akka.stream.alpakka.amqp.scaladsl.{AmqpSource, CommittableIncomingMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, KillSwitches}
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AmqpListener(amqpUri: AmqpUriConnectionProvider, queueName: String, lifecycle: ApplicationLifecycle,
                   onMessage: (String,Option[String]) => Try[String])(implicit val system: ActorSystem) {
  val logger = Logger(this.getClass)
  implicit val ec = system.dispatcher
  val decider: Decider = { ex =>
    logger.warn("AmqpFlows resuming because of ${ex.getClass.getCanonicalName} (${ex.getMessage})")
    logger.debug("AmqpFlows caught", ex)
    Resume
  }
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider))(system)
  type Committer = (Boolean) => Future[Done]
  type Payload = (String, Option[String], Committer)

  val messageHandler = Flow[Payload].map { case (body, mType, committer) =>
    (onMessage(body, mType), committer)
  }

  lazy val queueSource = AmqpSource.committableSource(NamedQueueSourceSettings(amqpUri, queueName)
    .withDeclarations(QueueDeclaration(queueName, durable = true)), bufferSize = 1)

  val unpack = Flow[CommittableIncomingMessage].map { cm =>
    val msgType = Option(cm.message.properties.getType)
    (cm.message.bytes.utf8String, msgType, (ack: Boolean) => if(ack) cm.ack(multiple = false) else cm.nack(multiple = false))
  }

  val acknowledge = Flow[(Try[String], Committer)] map {
    case (Success(response), committer) =>
      logger.debug(s"action returned Success ($response)")
      committer(true)
    case (Failure(t), _) =>
      logger.debug(s"action returned Failure: ${t.getMessage}")
  }

  lazy val ((((killer, _), _), _), listener) = queueSource
    .viaMat(KillSwitches.single)(Keep.right)
    .viaMat(unpack)(Keep.both)
    .viaMat(messageHandler)(Keep.both)
    .viaMat(acknowledge)(Keep.both)
    .toMat(Sink.ignore)(Keep.both)
    .run()

  def listen() = {
    logger.info(s"listening: [$queueName] (${amqpUri.uri})")
    listener onComplete {
      case Success(_) =>
        logger.info(s"[$queueName] listener finished")
      case Failure(t) =>
        logger.warn(s"[$queueName] listener failed: ${t.getMessage}")
    }
  }

  lifecycle.addStopHook { () =>
    Future {
      killer.shutdown()
    }
  }

}