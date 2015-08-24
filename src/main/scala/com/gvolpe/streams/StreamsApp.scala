package com.gvolpe.streams

import akka.actor.{ActorSystem, Status}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorAttributes, ActorMaterializer, OverflowStrategy, Supervision}
import com.gvolpe.streams.flows.{Event, EventPipelineFlow, FlowMessage}

object StreamsApp extends App with EventPipelineFlow {

  implicit val system = ActorSystem("EventProcessorSys")
  implicit val materializer = ActorMaterializer()

  val event = Event(12, "TENNIS", "providerName", "destName")
  val message = FlowMessage(Map("MatchSession" -> 5426 ,"a" -> "n"), event)
  val message2 = FlowMessage(Map("SomeHeader" -> true,"a" -> "n"), event)

  val source = Source.actorRef[FlowMessage](1000, OverflowStrategy.dropHead)
  val decider = ActorAttributes.supervisionStrategy(Supervision.restartingDecider)
  val pipelineActor = source.via(eventPipelineFlow.withAttributes(decider)).to(Sink.ignore).run()

  pipelineActor ! message
  pipelineActor ! message2
  pipelineActor ! Status.Success

}