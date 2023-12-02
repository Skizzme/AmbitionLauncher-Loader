package me.skizzme.cloud.socket.handlers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.skizzme.cloud.socket.api.IPacketHandler;
import me.skizzme.cloud.socket.packet.Packet;
import me.skizzme.loader.AClassLoader;
import me.skizzme.loader.AssetLoader;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ClassHandler extends IPacketHandler {

    private Executor executor = Executors.newFixedThreadPool(1);
    private AClassLoader loader;
    public boolean gotAll = false;
    public long totalObjects = 0;

    public ClassHandler(AClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public void receive(final Packet p)
    {
        if (p.getId() == 0x48) {
            AssetLoader.downloadAssets(p.jsonBody().getAsJsonObject("asset_index"));
        }
        if (p.getId() == 0x49) {
            this.totalObjects = p.jsonBody().get("total_size").getAsLong();
        }
        if (p.getId() == 0x50) {
            executor.execute(() -> {
                long st = System.nanoTime();
                JsonObject bobj = p.jsonBody();
                if (bobj.has("classes")) {
                    for (JsonElement e : bobj.getAsJsonArray("classes")) {
                        JsonObject o = e.getAsJsonObject();
                        byte[] data = null;
                        JsonElement d = o.get("data");
                        if (!d.isJsonNull()) {
                            byte[] input = Base64.getMimeDecoder().decode(d.getAsString());
                            byte[] buffer = new byte[o.get("size").getAsInt()];
                            Inflater decompressor = new Inflater();
                            decompressor.setInput(input);
                            decompressor.finished();
                            try {
                                decompressor.inflate(buffer);
                            } catch (DataFormatException ex) {
                                throw new RuntimeException(ex);
                            }
                            data = buffer;
                        }
                        this.loader.cacheClass(o.get("binary").getAsString(), data);
                    }
                } else if (bobj.has("resources")) {
                    for (JsonElement e : bobj.getAsJsonArray("resources")) {
                        JsonObject o = e.getAsJsonObject();
                        byte[] data = null;
                        JsonElement d = o.get("data");
                        if (!d.isJsonNull()) {
                            byte[] input = Base64.getMimeDecoder().decode(d.getAsString());
                            byte[] buffer = new byte[o.get("size").getAsInt()];
                            Inflater decompressor = new Inflater();
                            decompressor.setInput(input);
                            decompressor.finished();
                            try {
                                decompressor.inflate(buffer);
                            } catch (DataFormatException ex) {
                                throw new RuntimeException(ex);
                            }
                            data = buffer;
                        }
                        this.loader.cacheResource(o.get("path").getAsString(), data);
                    }
                }
            });
        }
        if (p.getId() == 0x51) {
            this.gotAll = true;
        }
    }
}
