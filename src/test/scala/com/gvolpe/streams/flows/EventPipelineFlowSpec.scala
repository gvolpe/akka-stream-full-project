package com.gvolpe.streams.flows

import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl.{FlowGraph, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

class EventPipelineFlowSpec extends StreamFlowSpec {

  object EventPipelineMock extends EventPipelineFlow

  private def flowGraph(message: FlowMessage) = FlowGraph.closed(collector, collector, collector)((_, _, _)) { implicit b => (out0, out1, out2) =>
    val epf = b.add(EventPipelineMock.partialEventPipeline)
    Source.single(message) ~> epf
                              epf.out(0) ~> out0
                              epf.out(1) ~> out1
                              epf.out(2) ~> out2
  }.run()

  "Event Pipeline Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the successful output" in withMessage(sessionHeaders) { message =>

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowGraph(message)

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

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowGraph(message)

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

      val (successfulOut, eventTypeSuppressed, eventDeletedLogger) = flowGraph(message)

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
