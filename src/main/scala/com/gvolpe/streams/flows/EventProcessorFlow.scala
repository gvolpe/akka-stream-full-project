package com.gvolpe.streams.flows

import akka.stream.UniformFanOutShape
import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl._
import com.gvolpe.streams.flows.TransformerFlows._
import com.gvolpe.streams.flows.utils.PartialFlowGraphUtils._

trait EventProcessorFlow {

  lazy val eventProcessorFlow = FlowGraph.partial() { implicit b =>
    val filteredMerge = b.add(Merge[FlowMessage](2))

    val originFilterFlow = b.add(filterPartialFlowGraph(_.event.origin == "providerName"))
    val destFilterFlow = b.add(filterPartialFlowGraph(_.event.destination == "destName"))
    val eventProcessor = b.add(partialFlow(process(_)))
    val sender = b.add(partialFlow(sendProcessedEvent(_)))
    val originLogger = b.add(partialFlow({ m => println(s"Origin Filtered: $m"); m }))
    val destLogger = b.add(partialFlow({ m => println(s"Destination Filtered: $m"); m }))
    val eventLogger = b.add(partialFlow({ m => println(s"Event Filtered: $m"); m }))

    originFilterFlow.out(0) ~> destFilterFlow
    originFilterFlow.out(1) ~> originLogger ~> filteredMerge
                               destFilterFlow.out(0) ~> eventProcessor ~> sender
                               destFilterFlow.out(1) ~> destLogger ~> filteredMerge
    filteredMerge ~> eventLogger

    UniformFanOutShape(originFilterFlow.in, sender.outlet, eventLogger.outlet)
  }.named("eventProcessorFlow")

}
