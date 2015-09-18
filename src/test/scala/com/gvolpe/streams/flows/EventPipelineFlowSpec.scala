package com.gvolpe.streams.flows

import com.gvolpe.streams.testkit.FlowTestKit

import scala.concurrent.Await
import scala.concurrent.duration._

class EventPipelineFlowSpec extends StreamFlowSpec {

  object EventPipelineMock extends EventPipelineFlow

  private val flowTestKit = FlowTestKit[FlowMessage]()
  
  "Event Pipeline Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the successful output using the final Event Pipeline Flow with the Sinks connected" in withMessage(sessionHeaders) { message =>

      val output = flowTestKit.graph(EventPipelineMock.eventPipelineFlow, message)

      val result = Await.result(output, 1000.millis)

      result.headers should contain key ("starting")
    }

    "Have only one message in the successful output using the final Event Pipeline Flow with the Sinks connected" in withMessageList() { messages =>

      val output = flowTestKit.graphSeq(EventPipelineMock.eventPipelineFlow, messages)

      val result = Await.result(output, 1000.millis)

      result should have size (1)
      result(0).headers should contain key ("starting")
      result(0) should be equals (messages(0))
    }

    "Have messages in the successful output" in withMessage(sessionHeaders) { message =>

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowTestKit.graph3(EventPipelineMock.partialEventPipeline, message)

      val result = Await.result(successfulOut, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(eventTypeSuppressed, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(eventDeletedLogger, 1000.millis)
      }

    }

    val golfEvent = Event(1, "GOLF", "one", "zero")

    "Have messages in the event type suppressed output" in withMessage(golfEvent) { message =>

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowTestKit.graph3(EventPipelineMock.partialEventPipeline, message)

      val result = Await.result(eventTypeSuppressed, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(successfulOut, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(eventDeletedLogger, 1000.millis)
      }
    }

    val tennisEvent = Event(2, "TENNIS", "one", "zero")

    "Have messages in the event deleted logger output" in withMessage(tennisEvent) { message =>

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowTestKit.graph3(EventPipelineMock.partialEventPipeline, message)

      val result = Await.result(eventDeletedLogger, 1000.millis)

      result.headers should contain key ("starting")

      // Should be an Empty stream
      intercept[NoSuchElementException] {
        Await.result(successfulOut, 1000.millis)
      }

      intercept[NoSuchElementException] {
        Await.result(eventTypeSuppressed, 1000.millis)
      }
    }

  }

}
