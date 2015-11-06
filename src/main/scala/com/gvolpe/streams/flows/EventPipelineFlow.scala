package com.gvolpe.streams.flows

import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl.{FlowGraph, Merge, Sink}
import akka.stream.{FlowShape, UniformFanOutShape}

trait EventPipelineFlow extends EventInputFlow
                        with HeadersValidationFlow
                        with EventTypeFilteredFlow
                        with EventProcessorFlow {

  lazy val eventPipelineFlow = FlowGraph.create() { implicit b =>
    val pipeline = b.add(partialEventPipeline)
    pipeline.out(1) ~> Sink.ignore
    pipeline.out(2) ~> Sink.ignore

    FlowShape(pipeline.in, pipeline.out(0))
  }.named("eventPipelineFlow")

  lazy val partialEventPipeline = FlowGraph.create() { implicit b =>
    val eventInput = b.add(eventInputFlow)
    val headersValidation = b.add(headersValidationFlow)
    val processorMerge = b.add(Merge[FlowMessage](2))
    val eventProcessor = b.add(eventProcessorFlow)

    eventInput.out(0) ~> processorMerge
    eventInput.out(1) ~> headersValidation ~> processorMerge
    processorMerge ~> eventProcessor

    UniformFanOutShape(eventInput.in, eventProcessor.out(0), eventInput.out(2), eventProcessor.out(1))
  }.named("partialEventPipeline")

}
