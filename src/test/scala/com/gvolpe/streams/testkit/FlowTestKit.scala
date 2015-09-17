package com.gvolpe.streams.testkit

import akka.stream.{FlowShape, Materializer, Graph, UniformFanOutShape}
import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl._

import scala.concurrent.Future

object FlowTestKit {
  def apply[T]()(implicit materializer : Materializer) = new FlowTestKit[T]()
}

private[testkit] class FlowTestKit[T](implicit materializer : Materializer) {

  private def sink[T]: Sink[T, Future[T]] = Flow[T].toMat(Sink.head)(Keep.right)
  private def sinkSeq[T]: Sink[T, Future[Seq[T]]] = Flow[T].grouped(100).toMat(Sink.head)(Keep.right)

  def graphSeq[T](flow: Graph[FlowShape[T, T], Unit], message: T): Future[Seq[T]] = {
    graphSeq(flow, Source.single(message))
  }

  def graphSeq[T](flow: Graph[FlowShape[T, T], Unit], source: Source[T, Unit]): Future[Seq[T]] = {
    source.via(flow).runWith(sinkSeq[T])
  }

  def graph[T](flow: Graph[FlowShape[T, T], Unit], message: T): Future[T] = {
    graph(flow, Source.single(message))
  }

  def graph[T](flow: Graph[FlowShape[T, T], Unit], source: Source[T, Unit]): Future[T] = {
    source.via(flow).runWith(sink[T])
  }

  def graph2[T](flow: Graph[UniformFanOutShape[T, T], Unit], message: T): (Future[T], Future[T]) = {
    graph2(flow, Source.single(message))
  }

  def graph2[T](flow: Graph[UniformFanOutShape[T, T], Unit], source: Source[T, Unit]): (Future[T], Future[T]) =
    FlowGraph.closed(sink[T], sink[T])((_, _)) { implicit b => (out0, out1) =>
      val inputFlow: UniformFanOutShape[T, T] = b.add(flow)
      source ~> inputFlow
                inputFlow.out(0) ~> out0
                inputFlow.out(1) ~> out1
    }.run()

  def graph3[T](flow: Graph[UniformFanOutShape[T, T], Unit], message: T): (Future[T], Future[T], Future[T]) = {
    graph3(flow, Source.single(message))
  }

  def graph3[T](flow: Graph[UniformFanOutShape[T, T], Unit], source: Source[T, Unit]): (Future[T], Future[T], Future[T]) =
    FlowGraph.closed(sink[T], sink[T], sink[T])((_, _, _)) { implicit b => (out0, out1, out2) =>
      val inputFlow: UniformFanOutShape[T, T] = b.add(flow)
      source ~> inputFlow
                inputFlow.out(0) ~> out0
                inputFlow.out(1) ~> out1
                inputFlow.out(2) ~> out2
  }.run()

  def graph4[T](flow: Graph[UniformFanOutShape[T, T], Unit], message: T): (Future[T], Future[T], Future[T], Future[T]) = {
    graph4(flow, Source.single(message))
  }

  def graph4[T](flow: Graph[UniformFanOutShape[T, T], Unit], source: Source[T, Unit]): (Future[T], Future[T], Future[T], Future[T]) =
    FlowGraph.closed(sink[T], sink[T], sink[T], sink[T])((_, _, _, _)) { implicit b => (out0, out1, out2, out3) =>
      val inputFlow: UniformFanOutShape[T, T] = b.add(flow)
      source ~> inputFlow
                inputFlow.out(0) ~> out0
                inputFlow.out(1) ~> out1
                inputFlow.out(2) ~> out2
                inputFlow.out(3) ~> out3
    }.run()

  def graph5[T](flow: Graph[UniformFanOutShape[T, T], Unit], message: T): (Future[T], Future[T], Future[T], Future[T], Future[T]) = {
    graph5(flow, Source.single(message))
  }

  def graph5[T](flow: Graph[UniformFanOutShape[T, T], Unit], source: Source[T, Unit]): (Future[T], Future[T], Future[T], Future[T], Future[T]) =
    FlowGraph.closed(sink[T], sink[T], sink[T], sink[T], sink[T])((_, _, _, _, _)) { implicit b => (out0, out1, out2, out3, out4) =>
      val inputFlow: UniformFanOutShape[T, T] = b.add(flow)
      source ~> inputFlow
                inputFlow.out(0) ~> out0
                inputFlow.out(1) ~> out1
                inputFlow.out(2) ~> out2
                inputFlow.out(3) ~> out3
                inputFlow.out(4) ~> out4
    }.run()

}
