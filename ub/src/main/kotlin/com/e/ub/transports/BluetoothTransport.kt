package com.e.ub.transports

import com.e.ub.Addr
import com.e.ub.Peer
import java.nio.ByteBuffer

class BluetoothTransport : Transport {
    /// The transports delegate.
    override var delegate: TransportDelegate?
        get() {
            TODO()
        }
        set(value) {}
    ///  The peers a specific transport can send messages to.
    override val peers: List<Peer>
        get() {
            TODO()
        }

    override fun send(message: ByteBuffer, to: Addr) {}
    /// Listen implements a function to receive messages being sent to a node.
    override fun listen() {}
}
