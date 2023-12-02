package me.skizzme.loader;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.skizzme.Main;
import me.skizzme.util.FileManager;
import me.skizzme.util.ThreadUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AssetLoader {

    private static Executor executor = Executors.newFixedThreadPool(5);
    private static int total = 0;
    private static AtomicInteger downloaded = new AtomicInteger(0);

    public static void downloadAssets(JsonObject index) {
        String indexPath = FileManager.getPath("assets", "indexes", "1.8.json");
        if (!FileManager.doesFileExist(indexPath)) {
            FileManager.writeFile(indexPath, new GsonBuilder().create().toJson(index));
        }
        Set<Map.Entry<String, JsonElement>> entrySet = index.get("objects").getAsJsonObject().entrySet();
        total = entrySet.size();
        for (Map.Entry<String, JsonElement> obj : entrySet) {
            executor.execute(() -> {
                String hash = obj.getValue().getAsJsonObject().get("hash").getAsString();
                String path = FileManager.getPath("assets", "objects", hash.substring(0, 2), hash);
                if (!FileManager.doesFileExist(path)) {
                    FileManager.downloadFile("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash, path);
                    System.out.println("Downloaded " + obj.getKey());
                }
                downloaded.getAndIncrement();
            });
        }
    }

    public static void waitForDownload() {
        while (downloaded.get() < total-1) {
            ThreadUtils.sleep(5);
        }
    }

}
