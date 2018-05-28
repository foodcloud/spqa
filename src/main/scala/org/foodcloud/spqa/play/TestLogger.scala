package org.foodcloud.spqa.play

import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.event.Level._

case class LogEntry(level: Level, message: Option[String] = None, marker: Option[Marker] = None, t: Option[Throwable] = None) {
  def withMessage(template: String, arguments: Any*) = copy(message=Some(template.format(arguments)))
  def withMessage(message: String) = copy(message=Some(message))
  override def toString = s"""$level ${marker.getOrElse("")}: ${message.getOrElse("")}"""
}

class TestLogger(name: String, onMessage: (LogEntry) => Unit,
                 val isTraceEnabled: Boolean = false,
                 val isDebugEnabled: Boolean = false,
                 val isInfoEnabled: Boolean = false,
                 val isWarnEnabled: Boolean = true,
                 val isErrorEnabled: Boolean = true) extends org.slf4j.Logger {
  override def getName = name

  override def isTraceEnabled(marker: Marker) = isTraceEnabled
  override def trace(msg: String) = onMessage(LogEntry(TRACE).withMessage(msg))
  override def trace(format: String, arg: Any) = onMessage(LogEntry(TRACE).withMessage(format, arg))
  override def trace(format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(TRACE).withMessage(format, arg1, arg2))
  override def trace(format: String, arguments: AnyRef*) = onMessage(LogEntry(TRACE).withMessage(format, arguments))
  override def trace(msg: String, t: Throwable) = onMessage(LogEntry(TRACE, t=Some(t)))
  override def trace(marker: Marker, msg: String) = onMessage(LogEntry(TRACE, marker=Some(marker)).withMessage(msg))
  override def trace(marker: Marker, format: String, arg: Any) = onMessage(LogEntry(TRACE, marker=Some(marker)).withMessage(format, arg))
  override def trace(marker: Marker, format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(TRACE, marker=Some(marker)).withMessage(format, arg1, arg2))
  override def trace(marker: Marker, format: String, argArray: AnyRef*) =  onMessage(LogEntry(TRACE, marker=Some(marker)).withMessage(format, argArray))
  override def trace(marker: Marker, msg: String, t: Throwable) = onMessage(LogEntry(TRACE, marker=Some(marker), t=Some(t)))

  override def isDebugEnabled(marker: Marker) = isDebugEnabled
  override def debug(msg: String) = onMessage(LogEntry(DEBUG).withMessage(msg))
  override def debug(format: String, arg: Any) = onMessage(LogEntry(DEBUG).withMessage(format, arg))
  override def debug(format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(DEBUG).withMessage(format, arg1, arg2))
  override def debug(format: String, arguments: AnyRef*) = onMessage(LogEntry(DEBUG).withMessage(format, arguments))
  override def debug(msg: String, t: Throwable) = onMessage(LogEntry(DEBUG, t=Some(t)))
  override def debug(marker: Marker, msg: String) = onMessage(LogEntry(DEBUG,marker=Some(marker)).withMessage(msg))
  override def debug(marker: Marker, format: String, arg: Any) = onMessage(LogEntry(DEBUG, marker=Some(marker)).withMessage(format, arg))
  override def debug(marker: Marker, format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(DEBUG, marker=Some(marker)).withMessage(format, arg1, arg2))
  override def debug(marker: Marker, format: String, argArray: AnyRef*) =  onMessage(LogEntry(DEBUG, marker=Some(marker)).withMessage(format, argArray))
  override def debug(marker: Marker, msg: String, t: Throwable) = onMessage(LogEntry(DEBUG, marker=Some(marker), t=Some(t)))

  override def isInfoEnabled(marker: Marker) = isInfoEnabled
  override def info(msg: String) = onMessage(LogEntry(INFO).withMessage(msg))
  override def info(format: String, arg: Any) = onMessage(LogEntry(INFO).withMessage(format, arg))
  override def info(format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(INFO).withMessage(format, arg1, arg2))
  override def info(format: String, arguments: AnyRef*) = onMessage(LogEntry(INFO).withMessage(format, arguments))
  override def info(msg: String, t: Throwable) = onMessage(LogEntry(INFO, t=Some(t)))
  override def info(marker: Marker, msg: String) = onMessage(LogEntry(INFO,marker=Some(marker)).withMessage(msg))
  override def info(marker: Marker, format: String, arg: Any) = onMessage(LogEntry(INFO, marker=Some(marker)).withMessage(format, arg))
  override def info(marker: Marker, format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(INFO, marker=Some(marker)).withMessage(format, arg1, arg2))
  override def info(marker: Marker, format: String, argArray: AnyRef*) =  onMessage(LogEntry(INFO, marker=Some(marker)).withMessage(format, argArray))
  override def info(marker: Marker, msg: String, t: Throwable) = onMessage(LogEntry(INFO, marker=Some(marker), t=Some(t)))

  override def isWarnEnabled(marker: Marker) = isWarnEnabled
  override def warn(msg: String) = onMessage(LogEntry(WARN).withMessage(msg))
  override def warn(format: String, arg: Any) = onMessage(LogEntry(WARN).withMessage(format, arg))
  override def warn(format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(WARN).withMessage(format, arg1, arg2))
  override def warn(format: String, arguments: AnyRef*) = onMessage(LogEntry(WARN).withMessage(format, arguments))
  override def warn(msg: String, t: Throwable) = onMessage(LogEntry(WARN, t=Some(t)))
  override def warn(marker: Marker, msg: String) = onMessage(LogEntry(WARN,marker=Some(marker)).withMessage(msg))
  override def warn(marker: Marker, format: String, arg: Any) = onMessage(LogEntry(WARN, marker=Some(marker)).withMessage(format, arg))
  override def warn(marker: Marker, format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(WARN, marker=Some(marker)).withMessage(format, arg1, arg2))
  override def warn(marker: Marker, format: String, argArray: AnyRef*) =  onMessage(LogEntry(WARN, marker=Some(marker)).withMessage(format, argArray))
  override def warn(marker: Marker, msg: String, t: Throwable) = onMessage(LogEntry(WARN, marker=Some(marker), t=Some(t)))

  override def isErrorEnabled(marker: Marker) = isErrorEnabled
  override def error(msg: String) = onMessage(LogEntry(ERROR).withMessage(msg))
  override def error(format: String, arg: Any) = onMessage(LogEntry(ERROR).withMessage(format, arg))
  override def error(format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(ERROR).withMessage(format, arg1, arg2))
  override def error(format: String, arguments: AnyRef*) = onMessage(LogEntry(ERROR).withMessage(format, arguments))
  override def error(msg: String, t: Throwable) = onMessage(LogEntry(ERROR, t=Some(t)))
  override def error(marker: Marker, msg: String) = onMessage(LogEntry(ERROR,marker=Some(marker)).withMessage(msg))
  override def error(marker: Marker, format: String, arg: Any) = onMessage(LogEntry(ERROR, marker=Some(marker)).withMessage(format, arg))
  override def error(marker: Marker, format: String, arg1: Any, arg2: Any) = onMessage(LogEntry(ERROR, marker=Some(marker)).withMessage(format, arg1, arg2))
  override def error(marker: Marker, format: String, argArray: AnyRef*) =  onMessage(LogEntry(ERROR, marker=Some(marker)).withMessage(format, argArray))
  override def error(marker: Marker, msg: String, t: Throwable) = onMessage(LogEntry(ERROR, marker=Some(marker), t=Some(t)))
}
