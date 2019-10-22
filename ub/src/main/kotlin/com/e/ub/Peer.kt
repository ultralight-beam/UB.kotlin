package com.e.ub


// @todo clean this up properly, currently very rough for testing purposes.
public/// Represents the nodes a transport can communicate with.
class Peer {
    /// The peers id.
    public val id: Addr
    /// The services a peer knows.
    val services: List<UBID>

    /// Initializes a peer with a specified id and list of known services.
    ///
    /// - Parameters:
    ///     - id: The peer id.
    ///     - services: The services a peer can knows.
    constructor(id: Addr, services: List<UBID>) {
        this.id = id
        this.services = services
    }
}