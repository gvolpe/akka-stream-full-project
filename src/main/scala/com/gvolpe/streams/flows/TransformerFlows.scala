package com.gvolpe.streams.flows

object TransformerFlows {

  // TODO: Add real behavior in all this functions
  def sendToExternalService(message: FlowMessage): FlowMessage = message
  def completeHeaders(message: FlowMessage): FlowMessage = message
  def process(message: FlowMessage): FlowMessage = message
  def sendProcessedEvent(message: FlowMessage): FlowMessage = message

}
