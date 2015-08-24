package com.gvolpe.streams.flows

import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl.{FlowGraph, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

class EventProcessorFlowSpec extends StreamFlowSpec {

  object EventProcessorMock extends EventProcessorFlow

  private def flowGraph(message: FlowMessage) = FlowGraph.closed(collector, collector)((_, _)) { implicit b => (out0, out1) =>
    val eif = b.add(EventProcessorMock.eventProcessorFlow)
    Source.single(message) ~> eif
                              eif.out(0) ~> out0
                              eif.out(1) ~> out1
  }.run()

  "Event Processor Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the sender output" in withMessage(sessionHeaders) { message =>

      val (senderOut, eventLoggerOut) = flowGraph(message)

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(eventLoggerOut, 1000.millis)
      }

      val result = Await.result(senderOut, 1000.millis)

      result shouldNot be (null)
    }

    val event = Event(12, "TENNIS", "providerName", "destination-X")

    "Have messages in the event logger output because is filtered by destination" in withMessage(event) { message =>

      val (senderOut, eventLoggerOut) = flowGraph(message)

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(senderOut, 1000.millis)
      }

      val result = Await.result(eventLoggerOut, 1000.millis)

      result shouldNot be (null)
    }

  }

}
