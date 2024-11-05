package me.skizzme.cloud;

import me.skizzme.Main;
import me.skizzme.cloud.socket.PacketHandler;
import me.skizzme.cloud.socket.Socket;
import me.skizzme.cloud.socket.packet.Packets;
import me.skizzme.cloud.socket.security.hashing.impl.SHAHashExchange;
import me.skizzme.hwid.HwidFactory;
import me.skizzme.hwid.component.impl.misc.ComputerNameHwidComponent;
import me.skizzme.hwid.component.impl.os.OSArchHwidComponent;
import me.skizzme.hwid.component.impl.os.OSNameHwidComponent;
import me.skizzme.hwid.component.impl.os.OSVersionHwidComponent;
import me.skizzme.hwid.component.impl.processor.ProcessorArchHwidComponent;
import me.skizzme.hwid.component.impl.processor.ProcessorCountHwidComponent;
import me.skizzme.hwid.component.impl.processor.ProcessorIdHwidComponent;
import me.skizzme.hwid.component.impl.processor.ProcessorLevelHwidComponent;
import me.skizzme.hwid.component.impl.user.UserDomainHwidComponent;
import me.skizzme.hwid.component.impl.user.UserNameHwidComponent;
import me.skizzme.util.FileManager;
import me.skizzme.util.JsonBuilder;
import sun.security.provider.SHA;

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
        final HwidFactory factory = new HwidFactory()
                .insert(
                        new ComputerNameHwidComponent(),
                        new UserNameHwidComponent(),
                        new UserDomainHwidComponent(),
                        new ProcessorIdHwidComponent(),
                        new ProcessorArchHwidComponent(),
                        new ProcessorLevelHwidComponent(),
                        new ProcessorCountHwidComponent(),
                        new OSNameHwidComponent(),
                        new OSArchHwidComponent(),
                        new OSVersionHwidComponent()
                );

       return factory.generate(true);
    }

}
