package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.IncomingDataHandlers.BaseIncomingDataTrafficHandler;
import org.json.JSONObject;

public class SSLFinishHandler extends BaseIncomingDataTrafficHandler {

    @Override
    public void handle(JSONObject data) {
        if (data.get("operation").equals("sslFinished")) {
            WindowLogger.getInstance().logSuccess(getClass().getName(), "SSL handshake completed.");
        } else super.handle(data);
    }
}
