package org.foodcloud.spqa.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import play.api.Logger

import scala.concurrent.Await
import scala.concurrent.duration.Duration


case class CannedResponse(code: Int = 200, text: Option[String] = None)

class HttpStubServer(endpoint: String,
                     response: CannedResponse = CannedResponse()) {
  val logger = Logger(this.getClass)
  implicit val system = ActorSystem("TestActorSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val http = Http()

  val uri = endpoint.contains(":@") match {
    case false =>
      Uri(endpoint)
    case true =>
      Uri(endpoint.replace(":@",s":${findPort()}"))
  }
  logger.debug(s"running on $uri")

  private var server: Option[ServerBinding] = None
  private var clientHandler: HttpRequest => Any =  (_: HttpRequest) => (): Unit
  private var responder = response
  def useResponse(response: CannedResponse) = {
    responder = response
    defaultHandler()
  }
  useResponse(response)

  private def rebind(handler: HttpRequest => HttpResponse) = {
    def bind = Await.result(http.bindAndHandleSync(handler, "localhost", uri.effectivePort) map (Option(_)), Duration.Inf)
    server = server match {
      case Some(s) =>
        Await.result(s.unbind(), Duration.Inf)
        bind
      case None =>
        bind
    }
  }

  private def defaultHandler() = {
    val handler: HttpRequest => HttpResponse = { _ =>
      responder match {
        case CannedResponse(code,Some(value)) =>
          HttpResponse(code, entity = value)
        case CannedResponse(code,_) =>
          HttpResponse(code)
      }
    }
    rebind(handler)
  }

  def handleRequests(validator: HttpRequest => Any) = {
    clientHandler = validator
    logger.debug(s"handling requests on ${uri.path}")
    val handler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
        sys.error("BOOM!")

      case request: HttpRequest if request.uri.path == uri.path =>
        clientHandler(request)
        responder match {
          case CannedResponse(code,Some(value)) =>
            HttpResponse(code, entity = value)
          case CannedResponse(code,_) =>
            HttpResponse(code)
        }

      case request: HttpRequest =>
        request.discardEntityBytes() // important to drain incoming HTTP Entity stream
        HttpResponse(404, entity = s"Unknown resource: ${request.uri.path}")
    }
    rebind(handler)
  }

  def shutdown() = {
    logger.debug(s"shutting down HttpStubServer")
    http.shutdownAllConnectionPools()
  }

  private def findPort() = {
    import java.net.ServerSocket
    val socket = new ServerSocket(0)
    val port = socket.getLocalPort
    socket.close()
    port
  }

}