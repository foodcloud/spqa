package org.foodcloud.spqa.amqp

import java.net.URI

import org.apache.qpid.server.SystemLauncher

import scala.collection.JavaConverters._

class QPIDBroker(virtualHost: String) {
  val credentials = "tester:secret"

  val socket = new java.net.ServerSocket(0)
  val port = socket.getLocalPort
  socket.close()
  val endpoint = s"amqp://$credentials@localhost:$port/$virtualHost"
  private val launcher = new SystemLauncher

  val configUrl = getClass.getClassLoader.getResource("qpid-broker.json")
  val uri = new URI(configUrl.getPath)
  val home = if (uri.getPath.endsWith("/")) uri.resolve("..") else uri.resolve(".")
  System.setProperty("qpid.amqp_port", port.toString)
  System.setProperty("qpid.home_dir", home.toString)
  System.setProperty("vhost.name", virtualHost)

  val brokerOptions: Map[String,Object] = Map(
    "type" -> "Memory",
    "initialConfigurationLocation" -> configUrl.toExternalForm,
    "initialSystemPropertiesLocation" -> s"file:$home/system.properties")
  launcher.startup(brokerOptions.asJava)

  def stopBroker() = {
    launcher.shutdown()
  }

}