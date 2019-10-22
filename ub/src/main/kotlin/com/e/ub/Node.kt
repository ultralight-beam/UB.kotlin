package com.e.ub

import com.e.ub.transports.Transport
import com.e.ub.transports.TransportDelegate
import com.google.protobuf.ByteString
import proto.Packet
import java.lang.StringBuilder
import java.nio.ByteBuffer


/// An ultralight beam node, handles the interaction with transports and services.
public class Node : TransportDelegate {
    /// The known transports for the node.
    var transports = mutableMapOf<String , Transport>()
    /// The nodes delegate.
    var delegate: NodeDelegate? = null

    /// Initializes a node.
    public constructor() {}

    override fun transport(transport: Transport, data: ByteBuffer, from: Addr) {
        // @todo message should probably be created here
        // @todo delegate should return something where we handle retransmission.
        // @todo if node delegate doesn't return anything success, send out the message?
        val packet = try {
            Packet.newBuilder().setBody(ByteString.copyFrom(data))
        } catch (e: Throwable) { null } ?: return
        delegate?.node(this, Message(protobuf = packet.build(), from = from))
    }

    /// Adds a new transport to the list of known transports.
    /// - Parameters:
    ///     - transport: The transport to be added.
    fun add(transport: Transport) {
        val id = String(StringBuilder(transport.toString()))
        if (transports[id] != null) {
            return
        }
        transports[id] = transport
        if (transport != null) transport.delegate = this
        transport.listen()
    }

    /// Removes a transport from the list of known transports.
    ///
    /// - Parameters:
    ///     - transport: The identifier of the transport to remove.
    fun remove(transport: String) {
        if (transports[transport] == null) {
            return
        }
        transports.remove(transport)
    }

    /// Sends a message through the current transports.
    ///
    /// - Parameters:
    ///     - message: The message to send.
    fun send(message: Message) {
        if (message.recipient.size == 0 && message.service.size == 0) {
            return
        }
        val data = try { message.toProto().toByteArray() } catch (e: Throwable) { null } ?: return
        transports.forEach { _, transport  ->
            val peers = transport.peers
            // @todo ensure that messages are delivered?
            // what this does is try to send a message to an exact target or broadcast it to all peers
            if (message.recipient.size != 0) {
                if (peers.any{ it.id.contentEquals(message.recipient) }) {
                    return@forEach transport.send(message = ByteBuffer.wrap(data), to = message.recipient)
                }
            }
            // what this does is send a message to anyone that implements a specific service
            if (message.service.size != 0) {
                val filtered = peers.filter { it.services.any { it.contentEquals(message.service) } }
                if (filtered.size > 0) {
                    val sends = flood(message, data = ByteBuffer.wrap(data), transport = transport, peers = filtered)
                    if (sends > 0) {
                        return@forEach
                    }
                }
            }
            flood(message, data = ByteBuffer.wrap(data), transport = transport, peers = peers)
        }
    }

    private fun flood(message: Message, data: ByteBuffer, transport: Transport, peers: List<Peer>) : Int {
        var sends = 0
        peers.forEach {
            if (it.id.contentEquals(message.from) || it.id.contentEquals(message.origin)) {
                return@forEach
            }
            sends += 1
            transport.send(message = data, to = it.id)
        }
        return sends
    }
}


