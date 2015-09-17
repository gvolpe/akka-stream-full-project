package com.gvolpe.streams.flows

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class StreamFlowSpec extends TestKit(ActorSystem("StreamFlowSpec"))
                     with WordSpecLike
                     with Matchers
                     with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

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
