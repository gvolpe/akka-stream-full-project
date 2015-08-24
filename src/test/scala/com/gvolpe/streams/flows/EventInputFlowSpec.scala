package com.gvolpe.streams.flows

import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl.{FlowGraph, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

class EventInputFlowSpec extends StreamFlowSpec {

  object EventInputMock extends EventInputFlow with EventTypeFilteredFlow

  private def flowGraph(message: FlowMessage) = FlowGraph.closed(collector, collector, collector)((_, _, _)) { implicit b => (out0, out1, out2) =>
    val eif = b.add(EventInputMock.eventInputFlow)
    Source.single(message) ~> eif
    eif.out(0) ~> out0
    eif.out(1) ~> out1
    eif.out(2) ~> out2
  }.run()

  "Event Input Flow" should {

    val sessionHeaders = Map("MatchSession" -> 5426)

    "Have messages in the filter output" in withMessage(sessionHeaders) { message =>

      val (filterOut, notFilterOut, suppressedOut) = flowGraph(message)

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

      val (filterOut, notFilterOut, suppressedOut) = flowGraph(message)

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

      val (filterOut, notFilterOut, suppressedOut) = flowGraph(message)

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
