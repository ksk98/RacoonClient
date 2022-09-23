package com.bots.RaccoonClient;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class Config {
    public static final String windowTitle = "RACCOON CLIENT";
    public static final String certfilePath = "raccoon.cert";
    public static final String localKeystorePath = "local_keystore.jks";
    public static final String localKeystorePassword = "notreallyimportant";
    // TODO: get rid of socket read timeout - either interrupt read when other action needs to be executed on socket or introduce separate thread for reading
    public static final int SocketTimeoutMS = 500;
    public static final DateTimeFormatter logTimestampFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ZoneId zoneId = ZoneId.of("Europe/Warsaw");
    public static final String cacheRootPath = System.getProperty("user.home") + "/RaccoonClientCache";
    public static final String appIconPath = "/Icon.png";
}
