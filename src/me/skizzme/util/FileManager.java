package me.skizzme.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {

    public static final String PATH = System.getenv("APPDATA") + File.separator + "AmbitionLauncher";

    public static void setup() {
        new File(PATH).mkdirs();
    }

    public static String getRawPath(String... dirs) {
        return String.join(File.separator, dirs);
    }

    public static String getPath(String... dirs) {
        return PATH + File.separator + String.join(File.separator, dirs);
    }

    public static String getPath(String dir) {
        return String.join(File.separator, PATH, dir);
    }

    public static byte[] readFile(String fileName) {
        File file = new File(fileName);
        byte[] bytes = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    public static void writeFile(String fileName, byte[] data) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fileName, String data) {
        writeFile(fileName, data.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean doesFileExist(String fileName) {
        return new File(fileName).exists();
    }

    public static void downloadFile(String url, String fileName) {
        try {
            InputStream in = new URL(url).openStream();
            File f = new File(fileName);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
