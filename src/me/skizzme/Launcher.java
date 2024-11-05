package me.skizzme;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.skizzme.cloud.CloudManager;
import me.skizzme.cloud.socket.handlers.ClassHandler;
import me.skizzme.cloud.socket.packet.Packet;
import me.skizzme.loader.AssetLoader;
import me.skizzme.loader.NativeLoader;
import me.skizzme.loader.AClassLoader;
import me.skizzme.util.FileManager;
import me.skizzme.util.ThreadUtils;

import java.util.Arrays;

public class Launcher {

    private AClassLoader classLoader;
    private NativeLoader nativeLoader;
    private CloudManager cloudManager;
    private ClassHandler classHandler;
    private boolean verbose;
    private long bytesRead;

    public void init(boolean verbose) {
        System.out.println("Initializing...");
        FileManager.setup();
        System.out.println(FileManager.getPath("assets"));
        this.verbose = verbose;
        this.nativeLoader = new NativeLoader();
        this.classLoader = new AClassLoader(verbose);
        this.cloudManager = new CloudManager();
        this.cloudManager.init();
        this.classHandler = new ClassHandler(this.classLoader);
        this.cloudManager.getHandler().registerHandler(this.classHandler);
    }

    public AClassLoader getClassLoader() {
        return this.classLoader;
    }

    public NativeLoader getNativeLoader() {
        return this.nativeLoader;
    }

    public void waitUntilAuthorized() {
        while (!this.cloudManager.getHandler().isAuthorized()) {
            ThreadUtils.sleep(50);
        }
    }

    public boolean hasGottenAll() {
        return this.classHandler.gotAll;
    }

    public void updateBytesRead(long bytesRead) {
        this.bytesRead+=bytesRead;
    }

    public void start(String[] args) {
        try {
            this.classLoader.cachedObjects = 0;
            long st = System.currentTimeMillis();
            System.out.println("Downloading...");

            JsonObject body = new JsonObject();
            JsonArray natives = new JsonArray();
            for (String s : nativeLoader.getExistingNatives()) {
                natives.add(new JsonPrimitive(s));
            }
            body.add("natives", natives);
            cloudManager.getHandler().sendPacket(new Packet(0x50, body));

            while (!this.classHandler.gotAll) {
                if (!verbose) {
                    System.out.print("" + ((int) (((double) (this.classLoader.cachedObjects) / this.classHandler.totalObjects) * 1000d)) / 10d + "%\r");
                }
                Thread.sleep(100);
            }
            System.out.println();
            System.out.println("Done. Elapsed: " + ((int) ((System.currentTimeMillis() - st) / 100)) / 10f);
            System.out.println("Writing natives");
            this.nativeLoader.loadNatives();
            System.out.println("Waiting for assets to download...");
            AssetLoader.waitForDownload();
            System.out.println("Starting..");
//            if (args.length == 0) {
//                classLoader.getClass("Start").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
//            } else {
//            classLoader.getClass("me.skizzme.Main").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
//            this.cloudManager.getSocket()
            classLoader.getClass("net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null, (Object) concat(args, new String[]{
                    "--version", "1.8",
                    "--accessToken", "0",
                    "--gameDir", FileManager.PATH + "\\",
                    "--assetsDir", FileManager.getPath("assets"),
                    "--assetIndex", "1.8"}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] concat(T[] first, T[] second) // definitely not straight from Start class
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
