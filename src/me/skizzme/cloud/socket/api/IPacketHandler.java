package me.skizzme.cloud.socket.api;


import me.skizzme.cloud.socket.packet.Packet;

public abstract class IPacketHandler {

    public abstract void receive(Packet p);
}
