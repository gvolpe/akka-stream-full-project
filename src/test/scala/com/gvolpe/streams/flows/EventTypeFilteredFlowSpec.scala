package com.gvolpe.streams.flows

import com.gvolpe.streams.testkit.FlowTestKit

import scala.concurrent.Await
import scala.concurrent.duration._

class EventTypeFilteredFlowSpec extends StreamFlowSpec {

  object EventTypeFiltered extends EventTypeFilteredFlow

  "Event Type Filtered Flow" should {

    "Have messages " in withMessage { message =>

      val future = FlowTestKit().graph(EventTypeFiltered.eventTypeFilteredFlow, message)

      val result = Await.result(future, 1000.millis)

      result.headers should (contain key ("suppressed") and contain value ("EventTypeFilter"))
    }
  }

}
