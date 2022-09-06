package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.IncomingDataHandlers.BaseIncomingDataTrafficHandler;
import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IncomingLogHandler extends BaseIncomingDataTrafficHandler {

    @Override
    public void handle(JSONObject data) {
        if (data.getString("operation").equals("log")) {
            try {
                WindowLogger.getInstance().logRemote((Log) SerializationUtil.fromString(data.getString("body")));
            } catch (IOException | ClassNotFoundException e) {
                WindowLogger.getInstance().logError(
                        getClass().getName(),
                        e.getMessage()
                );
            }
        } else super.handle(data);
    }
}
