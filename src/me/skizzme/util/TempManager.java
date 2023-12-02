package me.skizzme.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TempManager {

    private static String tmp_id;

    public static void clearOld() {
        String tDir = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tDir);
        for (File f : tmpDir.listFiles()) {
            if (f.isDirectory()) {
                String[] split = f.getPath().split("\\\\");
                if (split[split.length-1].startsWith("tmp_files_")) {
                    f.delete();
                }
            }
        }
    }

    public static String getFile(String tmp_path) {
        String tDir = System.getProperty("java.io.tmpdir");
        File tmpFolder = new File(tDir + "\\tmp_files_" + tmp_id);
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        File tmpFile = new File(tDir + "\\tmp_files_" + tmp_id + "\\" + tmp_path);
//        System.out.println(tmpFile + " EXISTS? " + tmpFile.exists());
        if (!tmpFile.exists()) {
            return null;
        }
        return tmpFile.getPath();
    }

    public static String writeTempFile(String tmp_path, byte[] data) {
        if (tmp_id == null) {
            tmp_id = StringUtils.randomString(8);
        }
        String tDir = System.getProperty("java.io.tmpdir");
        File tmpFolder = new File(tDir + "\\tmp_files_" + tmp_id);
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        File tmpFile = new File(tDir + "\\tmp_files_" + tmp_id + "\\" + tmp_path);
        try {
//            System.out.println(tmpFile + ", " + tmpFile.getParent());
            new File(tmpFile.getParent()).mkdirs();
            tmpFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tmpFile);
            stream.write(data);
            stream.close();
            return tmpFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void exit() {
        String tDir = System.getProperty("java.io.tmpdir");
        File tmpFolder = new File(tDir + "\\tmp_files_" + tmp_id);
        tmpFolder.delete();
    }

}
