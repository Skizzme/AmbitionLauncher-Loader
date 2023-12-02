package me.skizzme.cloud.socket;


import me.skizzme.Main;
import me.skizzme.cloud.socket.api.BaseSocketHandler;
import me.skizzme.cloud.socket.exception.NotConnectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Socket<T extends BaseSocketHandler> {

    public T handler;
    private OutputStream outgoing;
    private BufferedReader incoming;
    private java.net.Socket socket;
    private boolean connected = false;
    private byte[] currentRead = null;
    private int currentReadLength, currentReadLengthTarget;

    public Socket(T handler) {
        this.handler = handler;
        this.handler.setSocket(this);
    }

    public boolean connect(final String ip, final int port)
    {
        try {
            if (this.socket != null)
                this.socket.close();

            this.socket = new java.net.Socket(ip, port);
            this.socket.setSoTimeout(3000);
            this.socket.setReceiveBufferSize(524288);
            this.socket.setSendBufferSize(524288);
            this.outgoing = this.socket.getOutputStream();
            this.incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.connected = this.handler.handleConnect(this);
            this.currentRead = null;
            this.currentReadLengthTarget = 0;
            this.currentReadLength = 0;

            if (this.connected)
                new Thread(this::receiver).start();

            return this.connected;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return false;
    }

    public void receiver()
    {
        if (this.socket == null)
            return;

        while (this.socket.isConnected())
        {
            try
            {
                if (this.currentRead == null)
                {
                    byte[] length_prefix = new byte[8];
                    int r = readNBytes(length_prefix, 0, 8);
                    if (r == -1 || r == 0)
                    {
                        this.socket.close();
                        this.connected = false;
                        throw new SocketException("Input closed");
                    }

                    this.currentReadLengthTarget = (int) ByteBuffer.wrap(length_prefix).asLongBuffer().get();
                    if (this.currentReadLengthTarget < 0) {
                        this.currentReadLengthTarget = 0;
                        continue;
                    }
                    this.currentRead = new byte[this.currentReadLengthTarget];
                    continue;
                }

                int n = readNBytes(currentRead, currentReadLength, Math.min(currentReadLengthTarget-currentReadLength, 1024000));
                this.handler.update();

                if (n != -1)
                    this.currentReadLength += n;

                if (this.currentReadLength == this.currentReadLengthTarget)
                {
                    this.handler.handleIncoming(currentRead);
                    this.currentRead = null;
                    this.currentReadLengthTarget = 0;
                    this.currentReadLength = 0;
                }
            }
            catch (final IOException exception)
            {
                exception.printStackTrace();

//                System.out.println("client disconnected");
                this.connected = false;
//
//                try {
//                    this.socket.close();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }

                break;
            } catch (ArrayIndexOutOfBoundsException e) {
                this.connected = false;
                break;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private int readNBytes(byte[] b, int off, int len) throws IOException {
        int n = 0;
        while (n < len) {
            int count = this.socket.getInputStream().read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
    }

    public void send(final String data) throws NotConnectedException, IOException
    {
        this.sendBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    public void sendBytes(byte[] b_data) throws NotConnectedException, IOException
    {
        if (this.socket == null || !this.socket.isConnected())
        {
            this.connected = false;
            throw new NotConnectedException("Tried to send data without being connected.");
        }

        final byte[] header = ByteBuffer.allocate(8).putLong(b_data.length).array();
        final byte[] out = new byte[8 + b_data.length];

        System.arraycopy(header, 0, out, 0, header.length);
        System.arraycopy(b_data, 0, out, header.length, b_data.length);

        try {
            this.outgoing.write(out);
        } catch (SocketException e) {
            this.connected = false;
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public java.net.Socket getJavaSocket() {
        return socket;
    }

    public T getHandler() {
        return handler;
    }
}
