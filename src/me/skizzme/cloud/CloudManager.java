package me.skizzme.cloud;

import me.skizzme.cloud.socket.PacketHandler;
import me.skizzme.cloud.socket.Socket;
import me.skizzme.cloud.socket.security.hashing.impl.SHAHashExchange;

public class CloudManager {

    private Socket<PacketHandler> socket;
    private PacketHandler packetHandler;

    public void init() {
        this.packetHandler = new PacketHandler();
        this.socket = new Socket<>(packetHandler);
        new Thread(this::autoReconnect).start();
        System.out.println("HWID: " + getHWID());
    }

    private void autoReconnect() {
        while (true) {
            try {
                if (!socket.isConnected()) {
                    socket.connect("139.144.60.223", 10080);
//                    socket.connect("localhost", 10081);
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public PacketHandler getHandler() {
        return this.socket.getHandler();
    }

    public Socket<PacketHandler> getSocket() {
        return socket;
    }
    public static String getHWID()
    {
        return SHAHashExchange.INSTANCE.hash(System.getProperty("user.name"));
    }

}
