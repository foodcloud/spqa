package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.stream.ActorMaterializer
import org.foodcloud.spqa.http.{CannedResponse, HttpStubServer}
import org.scalatest.Matchers._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.Waiters._
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

import scala.concurrent.Await
import scala.concurrent.duration._

class HttpStubServerSpec extends FlatSpec with ScalaFutures with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestActorSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  "An instance of the stub sever" should "returned the service unavailable when invoked" in {
    val waiter = new Waiter

    val server = new HttpStubServer("http://127.0.0.1:1500/testServer")
    server.useResponse(CannedResponse(503))

    whenReady(Http().singleRequest(HttpRequest(uri = server.uri)), Timeout(3 seconds)) { response =>
      response.status shouldBe StatusCodes.ServiceUnavailable
      waiter.dismiss()
    }
    waiter.await(timeout(3 seconds), dismissals(1))
    Await.ready(server.shutdown(), Duration.Inf)
  }

  "An instance of the stub sever" should "returned the canned response when invoked" in {
    val waiter = new Waiter

    val server = new HttpStubServer("http://127.0.0.1:@/testServer")
    server.handleRequests { request: HttpRequest =>
      request.method shouldBe HttpMethods.POST
      val body = request.entity.toStrict(1 second) map (payload => payload.data.utf8String)
      whenReady(body, Timeout(3 seconds)) { message =>
        message shouldBe "Test POST Body"
        waiter.dismiss()
      }
      waiter.dismiss()
    }

    val request = HttpRequest(uri = server.uri, method = HttpMethods.POST).withEntity("Test POST Body")

    whenReady(Http().singleRequest(request), Timeout(3 seconds)) { response =>
      println(s"response=$response")
      response.status shouldBe StatusCodes.OK
      waiter.dismiss()
    }
    waiter.await(timeout(3 seconds), dismissals(1))
    Await.ready(server.shutdown(), Duration.Inf)
  }

  override def afterAll = system.terminate()

}
