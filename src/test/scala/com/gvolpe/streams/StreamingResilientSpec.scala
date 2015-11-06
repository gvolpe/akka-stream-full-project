package com.gvolpe.streams

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl.{FlowGraph, Keep, Sink, Source}
import akka.testkit.TestKit
import akka.util.Timeout
import com.gvolpe.streams.flows.utils.PartialFlowGraphUtils._
import com.gvolpe.streams.flows.{Event, FlowMessage, MessageHeader}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class StreamingResilientSpec extends TestKit(ActorSystem("StreamingResilientSpec"))
                              with WordSpecLike
                              with Matchers
                              with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private val restartingDecider = ActorAttributes.withSupervisionStrategy(Supervision.restartingDecider)
  private val actorSource = Source.actorRef(100, OverflowStrategy.dropHead)

  private val failureFlow = FlowGraph.create() { implicit b  =>
    val f1 = b.add(partialFlowWithHeader(MessageHeader("f1", "value1")))
    val f2 = b.add(partialFlow(generateException(_)))
    val f3 = b.add(partialFlowWithHeader(MessageHeader("f3", "value3")))

    f1 ~> f2 ~> f3

    FlowShape(f1.inlet, f3.outlet)
  }.withAttributes(restartingDecider)

  private def generateException(message: FlowMessage): FlowMessage = {
    if (message.headers.contains("throw")) throw new IllegalStateException()
    else message
  }

  private def defaultMessage = FlowMessage(Map.empty, Event(1, "RUGBY", "orig", "dest"))

  "The Streaming Flow" should {

    "Be resilient to failures" in {

      val counter = system.actorOf(CounterActorSink.props)
      val actorSink = Sink.actorRef(counter, "completed")
      val pipelineActor = actorSource.via(failureFlow).toMat(actorSink)(Keep.left).run()

      pipelineActor ! defaultMessage
      pipelineActor ! defaultMessage.copy(defaultMessage.headers + ("throw" -> ""), defaultMessage.event)

      pipelineActor ! defaultMessage
      pipelineActor ! defaultMessage

      Thread.sleep(1000)

      implicit val timeout = Timeout(5 seconds)

      val future = counter ? CounterActorSink.GetCount
      val result = Await.result(future, 1000.millis)

      // We sent 4 messages but one of them generated an exception and it was dropped
      result should be (3)
    }

  }

}

object CounterActorSink {
  case object GetCount
  def props = Props[CounterActorSink]
}

class CounterActorSink extends Actor {

  private var count = 0

  def receive = {
    case message: FlowMessage =>
      count = count + 1
    case CounterActorSink.GetCount =>
      sender ! count
  }

}

