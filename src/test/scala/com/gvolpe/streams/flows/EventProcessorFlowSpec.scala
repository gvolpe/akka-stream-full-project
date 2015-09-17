package com.gvolpe.streams.flows

import com.gvolpe.streams.testkit.FlowTestKit

import scala.concurrent.Await
import scala.concurrent.duration._

class EventProcessorFlowSpec extends StreamFlowSpec {

  object EventProcessorMock extends EventProcessorFlow

  "Event Processor Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the sender output" in withMessage(sessionHeaders) { message =>

      val (senderOut, eventLoggerOut) = FlowTestKit().graph2(EventProcessorMock.eventProcessorFlow, message)

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(eventLoggerOut, 1000.millis)
      }

      val result = Await.result(senderOut, 1000.millis)

      result shouldNot be (null)
    }

    val event = Event(12, "TENNIS", "providerName", "destination-X")

    "Have messages in the event logger output because is filtered by destination" in withMessage(event) { message =>

      val (senderOut, eventLoggerOut) = FlowTestKit().graph2(EventProcessorMock.eventProcessorFlow, message)

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(senderOut, 1000.millis)
      }

      val result = Await.result(eventLoggerOut, 1000.millis)

      result shouldNot be (null)
    }

  }

}
