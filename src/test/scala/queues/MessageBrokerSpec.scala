package queues

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.AmqpUriConnectionProvider
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.foodcloud.spqa.amqp.{AmqpListener, AmqpPublisher, QPIDBroker}
import org.scalatest._
import Matchers._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.Waiters._
import play.api.inject.DefaultApplicationLifecycle

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success

class MessageBrokerSpec extends FlatSpec with ScalaFutures with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestActorSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val brokerManager = new QPIDBroker
  val amqpEndpoint = ConfigValueFactory.fromAnyRef(brokerManager.endpoint)
  val config = ConfigFactory.load("test-application.conf")
    .withValue("amqp.uri", amqpEndpoint)

  val lifecycle = new DefaultApplicationLifecycle()

  "when a message is published to a queue, the listener" should
    "receive the message" in {
    val waiter = new Waiter

    val testMessage = "body=payload"

    val handler = (message: String, mType: Option[String]) => {
      message should equal("body=payload")
      mType shouldBe Some("test.message.type")
      waiter.dismiss()
      Success("ok")
    }
    val testQueue = "test-queue"
    val amqpConfig = AmqpUriConnectionProvider(config.getString("amqp.uri"))
    val publisher = new AmqpPublisher(amqpConfig)
    val listener = new AmqpListener(amqpConfig, testQueue, lifecycle, handler)
    listener.listen()

    whenReady(publisher.publish(testQueue, (testMessage, Some("test.message.type"))), Timeout(3 seconds)) { _ =>
      waiter.dismiss()
    }
    waiter.await(timeout(3 seconds), dismissals(2))
    Await.ready(lifecycle.stop, Duration.Inf)
  }



}
