package com.e.ub.transports

import com.e.ub.Addr
import java.nio.ByteBuffer


/// An interface used to handle events on the Transport.
public interface TransportDelegate {
    /// This method is called when a transport receives new data.
    ///
    /// - Parameters:
    ///     - transport: The transport that received a data.
    ///     - data: The received data.
    ///     - from: The peer from which the data was received.
    fun transport(transport: Transport, data: ByteBuffer, from: Addr)
}
