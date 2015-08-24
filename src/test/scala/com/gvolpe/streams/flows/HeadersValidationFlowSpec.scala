package com.gvolpe.streams.flows

import akka.stream.scaladsl.Source

import scala.concurrent.Await
import scala.concurrent.duration._

class HeadersValidationFlowSpec extends StreamFlowSpec {

  object HeadersValidation extends HeadersValidationFlow

  "Headers Validation Flow" should {

    "Have messages " in withMessage { message =>

      val future = Source.single(message)
        .via(HeadersValidation.headersValidationFlow)
        .runWith(collector)

      val result = Await.result(future, 1000.millis)

      result.headers should (contain key ("suppressed") and contain value ("HeadersValidation"))
    }

  }

}
