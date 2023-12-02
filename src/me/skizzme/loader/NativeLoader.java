package me.skizzme.loader;

import me.skizzme.Launcher;
import me.skizzme.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NativeLoader {

    private ArrayList<String> natives = new ArrayList<>();

    public void registerNative(String p) {
        natives.add(p);
    }

    public void loadNatives() throws IOException {
        String tDir = System.getProperty("java.io.tmpdir");
        File nativesFolder = new File(tDir + "\\natives_a");
        if (!nativesFolder.exists()) {
            nativesFolder.mkdirs();
        }
        for (String p : natives) {
            InputStream in = Main.launcher.getClassLoader().getResourceAsStream(p);
            if (in == null) {
                System.out.println("Native " + p + " was not found.");
                continue;
            }
            System.out.println("Loaded native \"" + p + "\"");
            byte[] buffer = new byte[1024];
            String[] split = p.split("/");
            File temp = new File(nativesFolder + "\\" + split[split.length-1]);
            if (temp.exists()) continue;
            FileOutputStream fos = new FileOutputStream(temp);

            int read;
            while((read = in.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            in.close();
            temp.deleteOnExit();
        }

        System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
    }

}
