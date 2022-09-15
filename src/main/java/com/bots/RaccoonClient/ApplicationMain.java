package com.bots.RaccoonClient;

import com.bots.RaccoonClient.Communication.ConnectionSocketManager;
import com.bots.RaccoonClient.Communication.SocketOperationQueueingService;
import com.bots.RaccoonClient.Views.ViewManager;

public class ApplicationMain {
    public static void main(String[] args) {
        CacheFilesManager.createCacheDirectoryRootDirectory();
        initializeSingletons();
    }

    private static void initializeSingletons() {
        ConnectionSocketManager.getInstance();
        SocketOperationQueueingService.getInstance();
        ViewManager.getInstance();
    }
}
