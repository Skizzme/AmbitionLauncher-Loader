package me.skizzme;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static Launcher launcher;

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        boolean verbose = false;
        if (args.length > 0) {
            String[] newArgs = new String[args.length-1];
            int ind = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-verbose")) {
                    verbose = true;
                    ind = i;
                } else {
                    newArgs[ind == -1 ? i : i-1] = args[i];
                }
            }
            args = newArgs;
        }
        System.out.println(Arrays.toString(args));
        launcher = new Launcher();
        launcher.init(verbose);
        launcher.waitUntilAuthorized();
        launcher.start(args);
    }

    public static ArrayList<File> getAllFilesInDirectory(String directory) {
        ArrayList<File> files = new ArrayList<>();
        for (File f : new File(directory).listFiles()) {
            if (f.getPath().endsWith(".class")) {
                files.add(f);
            }
            if (f.isDirectory()) {
                files.addAll(getAllFilesInDirectory(f.getPath()));
            }
        }
        return files;
    }

}
