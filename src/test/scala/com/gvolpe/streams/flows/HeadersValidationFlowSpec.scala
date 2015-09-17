package com.gvolpe.streams.flows

import com.gvolpe.streams.testkit.FlowTestKit

import scala.concurrent.Await
import scala.concurrent.duration._

class HeadersValidationFlowSpec extends StreamFlowSpec {

  object HeadersValidation extends HeadersValidationFlow

  "Headers Validation Flow" should {

    "Have messages " in withMessage { message =>

      val future = FlowTestKit().graph(HeadersValidation.headersValidationFlow, message)

      val result = Await.result(future, 1000.millis)

      result.headers should (contain key ("suppressed") and contain value ("HeadersValidation"))
    }

  }

}
