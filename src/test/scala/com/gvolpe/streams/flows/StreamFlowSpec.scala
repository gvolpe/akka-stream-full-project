package com.gvolpe.streams.flows

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future

class StreamFlowSpec extends TestKit(ActorSystem("StreamFlowSpec"))
                     with WordSpecLike
                     with Matchers
                     with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  def collector = genericCollector[FlowMessage]
  private def genericCollector[T]: Sink[T, Future[T]] = Flow[T].toMat(Sink.head)(Keep.right)

  val event = Event(12, "TENNIS", "providerName", "destName")
  val message = FlowMessage(Map.empty, event)

  def withMessage(testCode: FlowMessage => Unit) = {
    testCode(message)
  }

  def withMessage(event: Event)(testCode: FlowMessage => Unit) = {
    val newMessage = message.copy(message.headers, event)
    testCode(newMessage)
  }

  def withMessage(headers: Map[String, Any])(testCode: FlowMessage => Unit) = {
    val newMessage = message.copy(message.headers ++ headers, message.event)
    testCode(newMessage)
  }

}
