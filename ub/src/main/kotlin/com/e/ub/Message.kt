package com.e.ub

import com.google.protobuf.ByteString
import java.nio.ByteBuffer
import proto.Packet


/// Message represents the message sent between nodes.
public data class Message(
        // The message service.
        val service: UBID,
        // The recipient of the message.
        val recipient: Addr,
        // The sender of the message.
        val from: Addr,
        // The origin of the message, or the original sender.
        // Differs from the `sender` as that changes on every hop.
        val origin: Addr,
        // The raw message data.
        val message: ByteBuffer) {

    /// Initializes a Message with a packet and a from addr.
    ///
    /// - Parameters
    ///     - protobuf: The protocol buffer.
    ///     - from: The from address.
    constructor (protobuf: Packet, from: Addr) : this(
            protobuf.service.toByteArray(),
            protobuf.recipient.toByteArray(),
            from,
            protobuf.origin.toByteArray(),
            ByteBuffer.wrap(protobuf.body.toByteArray()))

    fun toProto() : Packet {
        return Packet.newBuilder()
                .setService(ByteString.copyFrom(service))
                .setRecipient(ByteString.copyFrom(recipient))
                .setOrigin(ByteString.copyFrom(origin))
                .setBody(ByteString.copyFrom(message))
                .build()
    }
}
// @todo encoding and decoding
