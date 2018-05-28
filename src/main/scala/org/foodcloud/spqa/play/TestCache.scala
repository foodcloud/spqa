package org.foodcloud.spqa.play

import akka.Done
import play.api.cache.AsyncCacheApi

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag


class TestCache(implicit val ec: ExecutionContext) extends AsyncCacheApi {
  private val cache: scala.collection.mutable.Map[String,Any] = scala.collection.mutable.Map()

  override def set(key: String, value: Any, expiration: Duration) = {
    cache(key) = value
    Future.successful(Done)
  }

  override def remove(key: String) = {
    cache -= key
    Future.successful(Done)
  }

  override def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: => Future[A])(implicit evidence$1: ClassTag[A]) = {
    val tagged = evidence$1.runtimeClass
    cache.get(key) match {
      case Some(value: A) if tagged.isInstance(value) =>
        Future.successful(value.asInstanceOf[A])
      case None =>
        orElse map { value =>
          cache(key) = value
          value
        }
    }
  }

  override def get[T](key: String)(implicit evidence$2: ClassTag[T]) = Future {
    cache(key) match {
      case value: T => Some(value)
      case _ => None
    }
  }

  override def removeAll() = {
    cache.clear()
    Future.successful(Done)
  }
}