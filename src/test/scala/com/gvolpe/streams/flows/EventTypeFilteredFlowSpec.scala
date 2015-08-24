package com.gvolpe.streams.flows

import akka.stream.scaladsl.Source

import scala.concurrent.Await
import scala.concurrent.duration._

class EventTypeFilteredFlowSpec extends StreamFlowSpec {

  object EventTypeFiltered extends EventTypeFilteredFlow

  "Event Type Filtered Flow" should {

    "Have messages " in withMessage { message =>

      val future = Source.single(message)
        .via(EventTypeFiltered.eventTypeFilteredFlow)
        .runWith(collector)

      val result = Await.result(future, 1000.millis)

      result.headers should (contain key ("suppressed") and contain value ("EventTypeFilter"))
    }
  }

}
