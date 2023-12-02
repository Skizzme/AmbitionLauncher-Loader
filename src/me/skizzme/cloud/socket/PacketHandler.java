package me.skizzme.cloud.socket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.skizzme.util.JsonBuilder;
import me.skizzme.util.ThreadUtils;
import me.skizzme.cloud.CloudManager;
import me.skizzme.cloud.socket.api.BaseSocketHandler;
import me.skizzme.cloud.socket.api.IPacketHandler;
import me.skizzme.cloud.socket.exception.NotConnectedException;
import me.skizzme.cloud.socket.packet.Packet;
import me.skizzme.cloud.socket.packet.Packets;
import me.skizzme.cloud.socket.security.encryption.impl.AESEncryptionExchange;
import me.skizzme.cloud.socket.security.encryption.impl.RSAEncryptionExchange;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class PacketHandler extends BaseSocketHandler {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    private boolean secured = false;
    private AESEncryptionExchange aes;
    private RSAEncryptionExchange rsa;
    private ArrayList<IPacketHandler> handlers = new ArrayList<>();
    private String lastInSalt, lastOutSalt;
    private Packet lastReceivedPacket;
    private LinkedBlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<>();
    private int keepAliveBuffer;
    private boolean authorized;

    public PacketHandler()
    {
        new Thread(this::queue_runner).start();
    }

    public void registerHandler(IPacketHandler h) {
        this.handlers.add(h);
    }

    public void update() {
        this.keepAliveBuffer = 0;
    }

    @Override
    public void handleIncoming(final byte[] data)
    {
        try {
            if (!this.secured) {
                final Packet packet = Packet.read(new String(Base64.getMimeDecoder().decode(Objects.requireNonNull(this.rsa.decrypt(data)))));

                if (packet != null)
                    this.handlePacket(packet);
                else
                    System.out.println("RECEIVED NULL PACKET");

                return;
            }

            final Packet packet = Packet.read(new String(Base64.getDecoder().decode(Objects.requireNonNull(this.aes.decrypt(data)))));

            if (packet != null)
                this.handlePacket(packet);
            else
                System.out.println("RECEIVED NULL PACKET");
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    public void authorize()
    {
        this.sendPacket(new Packet(Packets.AUTHORIZATION, new JsonBuilder().addProperty("hwid", CloudManager.getHWID()).build()));
    }

    public void handlePacket(final Packet packet)
    {
        this.lastReceivedPacket = packet;
        if (!this.secured)
        {
            if (packet.getId() != Packets.HANDSHAKE)
            {
                System.out.println("received communication packet before handshake completed!");
                Runtime.getRuntime().exit(-1);
                return;
            }

            JsonObject object = packet.jsonBody();

            if (object != null && object.has("key"))
            {
                final String key = object.get("key").getAsString();
                this.lastOutSalt = this.generateNewSalt(key.substring(key.length()/2));
                this.lastInSalt = key.substring(0, key.length()/2);
                final SecretKey secretKey = AESEncryptionExchange.decode(key);

                if (secretKey != null)
                {
                    this.aes = new AESEncryptionExchange(secretKey);
                    this.secured = true;

                    System.out.println("HANDSHAKE COMPLETE");

                    ThreadUtils.sleep(200);
                    new Thread(this::keepAlive).start();
                    this.authorize();
                }
            }
        }
        else
        {
            if (packet.getId() == Packets.AUTHORIZATION_RESPONSE) {
                this.authorized = true;
            }
            if (!this.generateNewSalt(this.lastInSalt).equals(packet.getSalt())) {
                System.out.println("no match. got:" + packet.getSalt() + ", expected:" + this.generateNewSalt(this.lastInSalt) + ", last:" + this.lastInSalt);
                Runtime.getRuntime().exit(0);
            }

            this.lastInSalt = this.generateNewSalt(this.lastInSalt);

            this.keepAliveBuffer = 0;

            for (IPacketHandler handler : this.handlers) {
                new Thread(() -> handler.receive(packet)).start();
            }
        }
    }

    public void keepAlive()
    {
        while (this.socket.isConnected())
        {
            try
            {
                if (this.keepAliveBuffer++ > 1) {
                    System.out.println("MISSED KEEPALIVE");
//                    this.socket.getJavaSocket().close();
                }

                this.sendPacket(new Packet(Packets.KEEP_ALIVE, ""));
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
            }

            ThreadUtils.sleep(1000);
        }
    }

    public Packet sendRequest(Packet packet) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            return executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 10_000L)
                {
                    if (this.lastReceivedPacket.getId() == Packets.RESPONSE) {
                        JsonObject obj = this.lastReceivedPacket.jsonBody();
                        if (obj.has("original_salt") && obj.get("original_salt").getAsString().equals(packet.getSalt())) {
                            return lastReceivedPacket;
                        }
                    }
                    ThreadUtils.sleep(1);
                }

                return new Packet(0);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean handleConnect(Socket<?> socket)
    {
        this.rsa = null;
        this.aes = null;
        this.secured = false;
        this.keepAliveBuffer = 0;

        try
        {
            final KeyPair keyPair = RSAEncryptionExchange.newKeyPair(2048);

            if (keyPair != null)
            {
                this.rsa = new RSAEncryptionExchange(keyPair);

                System.out.println("SENDING HANDSHAKE");
                this.sendPacket(new Packet(Packets.HANDSHAKE, new JsonBuilder().addProperty("key", RSAEncryptionExchange.encode(keyPair.getPublic())).build()));
                return true;
            }
        }
        catch (final Exception exception)
        {
            System.out.println("EXITING");
            exception.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }

        return false;
    }

    public String generateNewSalt(String last) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(last.getBytes());

            StringBuilder newSalt = new StringBuilder();
            StringBuilder asciiLetters = new StringBuilder();
            for (char c = 'a'; c <= 'z'; c++) {
                asciiLetters.append(c);
            }
            for (char c = 'A'; c <= 'Z'; c++) {
                asciiLetters.append(c);
            }

            for (int i = 0; i < hashBytes.length; i++) {
                int byteValue = hashBytes[i] & 0xD7; // Convert byte to positive integer value
                int index = byteValue % asciiLetters.length();
                newSalt.append(asciiLetters.charAt(index));
            }

            return newSalt.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void queue_runner() {
        while (true) {
            Packet packet = sendQueue.poll();
            if (packet == null) {
                continue;
            }
            send(packet);
        }
    }

    public void sendPacket(final Packet packet) {
        try {
            this.sendQueue.put(packet);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(final Packet packet)
    {
        String data = packet.getBody();

        if (!this.secured && packet.getId() != Packets.HANDSHAKE)
        {
            System.out.println("tried communicating before handshake");
//            Runtime.getRuntime().exit(-1);
            return;
        }

        if (data != null)
        {
            if (this.secured && this.aes != null)
            {
                packet.setSalt(this.lastOutSalt);
                try
                {
                    this.socket.sendBytes(this.aes.encrypt(GSON.toJson(packet.serialize())));
                    this.lastOutSalt = generateNewSalt(packet.getSalt());
                }
                catch (final NotConnectedException | IOException exception)
                {
                    throw new RuntimeException(exception);
                }
            }
            else
            {
                try
                {
                    this.socket.send(GSON.toJson(packet.serialize()));
                }
                catch (final NotConnectedException | IOException exception)
                {
                    throw new RuntimeException(exception);
                }
            }
        }
    }

    public boolean isAuthorized() {
        return this.authorized;
    }
}
