package me.skizzme.cloud.socket.api;

import me.skizzme.cloud.socket.Socket;
import me.skizzme.cloud.socket.packet.Packet;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseSocketHandler
{

    protected Socket<?> socket;

    public abstract void handleIncoming(byte[] data);
    public abstract boolean handleConnect(final Socket<?> socket);

    public abstract void update();

    public void setSocket(final Socket<?> socket)
    {
        if (this.socket == null)
            this.socket = socket;
    }

    public Socket<?> getSocket()
    {
        return this.socket;
    }

}
