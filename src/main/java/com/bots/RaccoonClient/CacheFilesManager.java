package com.bots.RaccoonClient;

import com.bots.RaccoonClient.Loggers.WindowLogger;
import org.json.JSONObject;

import java.io.*;

public abstract class CacheFilesManager {
    public static final String loginCacheFilePath = Config.cacheRootPath + "/login_cache.json";

    public static void createCacheDirectoryRootDirectory() {
        File file = new File(Config.cacheRootPath);
        file.mkdirs();
    }

    /**
     * @return JSONObject containing cache or null if cache could not be retrieved
     */
    public static JSONObject getCacheIfExists(String path) {
        try (FileReader reader = new FileReader(path)) {
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1)
                builder.append((char) c);

            return new JSONObject(builder.toString());
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            WindowLogger.getInstance().logError("CACHE MANAGER", "Cache under path " + path + " could not be read.");
        }

        return null;
    }

    public static void writeCache(String path, JSONObject content) {
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            WindowLogger.getInstance().logError("CACHE MANAGER", "Cache under path " + path + " could not be created.");
        }

        try (FileWriter writer = new FileWriter(path)){
            writer.write(content.toString());
            writer.flush();
        } catch (IOException e) {
            WindowLogger.getInstance().logError("CACHE MANAGER", "Cache under path " + path + " could not be written.");
        }
    }
}
