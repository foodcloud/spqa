package org.foodcloud.spqa.play

import play.api.i18n.DefaultMessagesApi

case class MessageBuilder(messages: Map[String,Map[String,String]] = Map()) {
  def from(filePath: String, langKey: String) = {
    val inputStream = getClass.getResourceAsStream(filePath)
    val definitions = scala.io.Source.fromInputStream(inputStream).getLines map { line =>
      val parts = line.split("=")
      parts(0).trim -> parts(1).trim
    }
    MessageBuilder(messages ++ Map(langKey -> definitions.toMap))
  }

  def build() = new DefaultMessagesApi(messages)
}
