package com.e.ub.transports

import com.e.ub.Addr
import com.e.ub.Peer
import com.e.ub.UBID
import java.nio.ByteBuffer

/// Transports are used to send messages between nodes using different methods, e.g. wifi direct or bluetooth.
public interface Transport {
    /// The transports delegate.
    var delegate: TransportDelegate?
    ///  The peers a specific transport can send messages to.
    val peers: List<Peer>
    /// Send implements a function to send messages between nodes using the transport.
    ///
    /// - Parameters:
    ///     - message: The message to send.
    ///     - to: The node to which to send the message.
    fun send(message: ByteBuffer, to: Addr)
    /// Listen implements a function to receive messages being sent to a node.
    fun listen()
    /// Return peers that support a particular service
    fun peersWithService(service: UBID): List<Peer> {
        return peers.filter { peer -> peer.services.any { it.contentEquals(service) } }
    }
}
