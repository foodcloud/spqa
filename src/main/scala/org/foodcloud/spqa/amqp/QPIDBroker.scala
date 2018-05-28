package org.foodcloud.spqa.amqp

import java.nio.file.FileSystems

import org.apache.qpid.server.SystemLauncher
import play.api.Logger

import scala.collection.JavaConverters._

class QPIDBroker {
  val logger = Logger(this.getClass)
  val configUrl = getClass.getClassLoader.getResource("qpid-broker.json")
  logger.debug(s"source=${configUrl.toExternalForm}")

  val socket = new java.net.ServerSocket(0)
  val port = socket.getLocalPort
  socket.close()
  val endpoint = s"amqp://guest:guest@localhost:$port/amqp"
  private val launcher = new SystemLauncher

  val home = FileSystems.getDefault.getPath("src/test/resources").toAbsolutePath
  System.setProperty("qpid.amqp_port", port.toString)
  System.setProperty("qpid.home_dir", home.toString)

  val brokerOptions: Map[String,Object] = Map(
    "type" -> "Memory",
    "initialConfigurationLocation" -> configUrl.toExternalForm)
  launcher.startup(brokerOptions.asJava)
  logger.debug(s"QPIDBroker ${launcher.getSystemPrincipal.getName} started on $endpoint")

  def stopBroker() = {
    launcher.shutdown()
    logger.debug(s"QPIDBroker ${launcher.getSystemPrincipal.getName} stopped")
  }

}