package com.bots.RacoonClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Config {
    public static final String windowTitle = "RACOON CLIENT";
    public static final String certfilePath = "racoon.cert";
    public static final String localKeystorePath = "local_keystore.jks";
    public static final String localKeystorePassword = "notreallyimportant";
    public static final int SocketTimeoutMS = 5000;
    public static final DateTimeFormatter logTimestampFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ZoneId zoneId = ZoneId.of("Europe/Warsaw");
}
