package com.gvolpe.streams.flows

import akka.stream.FlowShape
import akka.stream.scaladsl.FlowGraph
import akka.stream.scaladsl.FlowGraph.Implicits._
import com.gvolpe.streams.flows.TransformerFlows._
import com.gvolpe.streams.flows.utils.PartialFlowGraphUtils._

trait HeadersValidationFlow {

  lazy val headersValidationFlow = FlowGraph.partial() { implicit b =>
    val logger = b.add(partialFlowWithHeader(MessageHeader("suppressed", "HeadersValidation")))
    val headersCompletion = b.add(partialFlow(completeHeaders(_)))

    logger ~> headersCompletion

    FlowShape(logger.inlet, headersCompletion.outlet)
  }.named("headersValidationFlow")

}
