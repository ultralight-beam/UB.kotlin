package com.e.ub


/// An interface used to handle events on the Node.
public interface NodeDelegate {
    /// This method is called when a node receives a message.
    ///
    /// - Parameters:
    ///     - node: The node that received the message.
    ///     - message: The received message.
    fun node(node: Node, message: Message)
}
