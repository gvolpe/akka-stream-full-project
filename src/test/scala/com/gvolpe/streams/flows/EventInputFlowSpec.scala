package com.gvolpe.streams.flows

import com.gvolpe.streams.testkit.FlowTestKit
import scala.concurrent.Await
import scala.concurrent.duration._

class EventInputFlowSpec extends StreamFlowSpec {

  object EventInputMock extends EventInputFlow with EventTypeFilteredFlow

  "Event Input Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the filter output" in withMessage(sessionHeaders) { message =>

      val (filterOut, notFilterOut, suppressedOut) = FlowTestKit().graph3(EventInputMock.eventInputFlow, message)

      val result = Await.result(filterOut, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(notFilterOut, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(suppressedOut, 1000.millis)
      }

    }

    "Have messages in the not filter output" in withMessage { message =>

      val (filterOut, notFilterOut, suppressedOut) = FlowTestKit().graph3(EventInputMock.eventInputFlow, message)

      val result = Await.result(notFilterOut, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(filterOut, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(suppressedOut, 1000.millis)
      }
    }

    val event = Event(1, "GOLF", "one", "zero")

    "Have messages in the suppressed output" in withMessage(event) { message =>

      val (filterOut, notFilterOut, suppressedOut) = FlowTestKit().graph3(EventInputMock.eventInputFlow, message)

      val result = Await.result(suppressedOut, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(notFilterOut, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(filterOut, 1000.millis)
      }

    }

  }

}
