package me.skizzme;

import me.skizzme.cloud.CloudManager;
import me.skizzme.cloud.socket.handlers.ClassHandler;
import me.skizzme.cloud.socket.packet.Packet;
import me.skizzme.loader.NativeLoader;
import me.skizzme.loader.AClassLoader;
import me.skizzme.util.TempManager;
import me.skizzme.util.ThreadUtils;

public class Launcher {

    private AClassLoader classLoader;
    private NativeLoader nativeLoader;
    private CloudManager cloudManager;
    private ClassHandler classHandler;
    private boolean verbose;

    public void init(boolean verbose) {
        System.out.println("Initializing...");
        TempManager.clearOld();
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

    public void start(String[] args) {
        try {
            this.classLoader.cachedObjects = 0;
            long st = System.currentTimeMillis();
            System.out.println("Downloading...");
            cloudManager.getHandler().sendPacket(new Packet(0x50, ""));

            while (!this.classHandler.gotAll) {
                if (!verbose) {
                    System.out.print("" + ((int) (((double) (this.classLoader.cachedObjects) / this.classHandler.totalObjects) * 1000d)) / 10d + "%\r");
                }
                Thread.sleep(100);
            }
            System.out.println();
            System.out.println("Done. Elapsed: " + ((int) ((System.currentTimeMillis() - st) / 100)) / 10f);
            System.out.println("Writing temp natives");
            this.nativeLoader.loadNatives();
            System.out.println("Starting..");
            if (args.length == 0) {
                classLoader.getClass("Start").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            } else {
                classLoader.getClass("net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null, (Object) args);
            }
            TempManager.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
