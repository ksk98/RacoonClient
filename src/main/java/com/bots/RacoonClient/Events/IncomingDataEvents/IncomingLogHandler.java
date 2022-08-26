package com.bots.RacoonClient.Events.IncomingDataEvents;


import com.bots.RacoonClient.WindowLogger;
import com.bots.RacoonShared.Events.IncomingDataEvents.BaseIncomingDataTrafficHandler;
import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.Logging.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IncomingLogHandler extends BaseIncomingDataTrafficHandler {
    @Override
    public void handle(JSONObject response) {
        if (response.getString("operation").equals("log")) {
            try {
                WindowLogger.getInstance().log((Log) SerializationUtil.fromString(response.getString("object")));
            } catch (IOException | ClassNotFoundException e) {
                WindowLogger.getInstance().logError(e.getMessage());
            }
        } else super.handle(response);
    }
}
