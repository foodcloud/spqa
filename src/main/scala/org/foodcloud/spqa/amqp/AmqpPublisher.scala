package org.foodcloud.spqa.amqp

import akka.actor.ActorSystem
import akka.stream.Supervision.{Decider, Resume}
import akka.stream.alpakka.amqp._
import akka.stream.alpakka.amqp.scaladsl.AmqpSink
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.rabbitmq.client.AMQP.BasicProperties
import play.api.Logger
import play.api.libs.json.JsValue

class AmqpPublisher(amqpUri: AmqpUriConnectionProvider)(implicit val system: ActorSystem) {
  val logger = Logger(this.getClass)
  implicit val ec = system.dispatcher
  val decider: Decider = { ex =>
    logger.warn("AmqpFlows resuming because of ${ex.getClass.getCanonicalName} (${ex.getMessage})")
    logger.debug("AmqpFlows caught", ex)
    Resume
  }
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider))(system)
  type Payload = (JsValue, String, (String, String))

  val toOutgoing = Flow[(String, Option[String])].map { case (body, mType) =>
    val tProp = new BasicProperties.Builder()
    val props = mType match {
      case Some(t) => tProp.`type`(t)
      case None => tProp
    }
    OutgoingMessage(ByteString(body.toString), immediate = false, mandatory = false, Some(props.build()))
  }

  def publish(queueName: String, message: (String, Option[String])) = {
    logger.debug(s"publishing $message -> [$queueName]")
    val queue = QueueDeclaration(queueName, durable = true)
    val queueSink = AmqpSink(AmqpSinkSettings(amqpUri)
      .withRoutingKey(queue.name)
      .withDeclarations(queue))
    Source.single(message) via toOutgoing runWith queueSink
  }
}