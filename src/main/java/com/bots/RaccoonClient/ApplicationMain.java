package com.bots.RaccoonClient;

import com.bots.RaccoonClient.Views.ViewManager;

public class ApplicationMain {
    public static void main(String[] args) {
        CacheFilesManager.createCacheDirectoryRootDirectory();
        ViewManager.getInstance();
    }
}
