package com.e.ub

import com.e.ub.transports.Transport
import com.e.ub.transports.TransportDelegate
import com.google.protobuf.ByteString
import proto.Packet
import java.lang.StringBuilder
import java.nio.ByteBuffer

/// An ultralight beam node, handles the interaction with transports and services.
class Node : TransportDelegate {
    /// The known transports for the node.
    var transports = mutableMapOf<String, Transport>()
        get() = field
        set(value) {
            field = value
        }
    /// The nodes delegate.
    private var delegate: NodeDelegate? = null

    /// Initializes a node.
    constructor()

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
        transport.delegate = this
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
        if (message.recipient.isNotEmpty() && message.service.isNotEmpty()) {
            val data = try { message.toProto().toByteArray() } catch (e: Throwable) { null } ?: return
            transports.forEach { (_, transport) ->
                // try to send a message to an exact target
                if (anyRecipients(message, transport)) {
                    return@forEach transport.send(message = ByteBuffer.wrap(data), to = message.recipient)
                }
                // send a message to anyone that implements a specific service
                flood(message,
                        data = ByteBuffer.wrap(data),
                        transport = transport,
                        peers = peersWithService(message, transport))
            }
        }
    }

    private fun peersWithService(message: Message, transport: Transport): List<Peer> {
        var result = listOf<Peer>()
        if (message.service.isNotEmpty()) {
            result = transport.peersWithService(message.service)
        }
        return result
    }

    private fun anyRecipients(message: Message, transport: Transport): Boolean {
        var result = false
        if (message.recipient.isNotEmpty()) {
            result = transport.peers.any { it.id.contentEquals(message.recipient) }
        }
        return result
    }

    private fun flood(message: Message, data: ByteBuffer, transport: Transport, peers: List<Peer>): Int {
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
