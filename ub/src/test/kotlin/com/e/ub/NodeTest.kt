package com.e.ub

import com.e.ub.transports.BluetoothTransport
import com.e.ub.transports.Transport
import com.e.ub.transports.TransportDelegate
import org.junit.*
import org.junit.Assert.*
import java.lang.StringBuilder
import java.nio.ByteBuffer

class NodeTest {

    @org.junit.Test
    fun testAddTransport() {
        val transport = BluetoothTransport()
        val node = Node()
        node.add(transport = transport)
        assert(node.transports.keys.first() == String(StringBuilder(transport.toString())))
        assert((node.transports.values.first() as? Transport) === transport)
    }

    @org.junit.Test
    fun testRemoveTransport() {
        val transport = BluetoothTransport()
        val node = Node()
        node.add(transport = transport)
        assert(node.transports.keys.first() == String(StringBuilder(transport.toString())))
        node.remove(transport =  String(StringBuilder(transport.toString())))
        assert(node.transports.values.isEmpty())
    }

    @org.junit.Test
    fun testEncode() {
    }
}

