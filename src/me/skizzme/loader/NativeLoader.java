package me.skizzme.loader;

import me.skizzme.Launcher;
import me.skizzme.Main;
import me.skizzme.util.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class NativeLoader {

    private CopyOnWriteArrayList<String> natives = new CopyOnWriteArrayList<>();

    public void registerNative(String p) {
        natives.add(p);
    }

    public void loadNatives() throws IOException {
        String nativesPath = FileManager.getPath("natives");
        for (String p : natives) {
            byte[] bytes = Main.launcher.getClassLoader().getResourceBytes(p);
            if (bytes == null) {
                System.out.println("Native " + p + " was not found.");
                continue;
            }
            String[] split = p.split("/");
            String nativePath = FileManager.getPath("natives", split[split.length-1]);
            if (FileManager.doesFileExist(nativePath)) {
                continue;
            }
            FileManager.writeFile(nativePath, bytes);
            System.out.println("Loaded native \"" + p + "\"");
        }

        System.setProperty("org.lwjgl.librarypath", nativesPath);
    }

}
