package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Main.MessageOutput;
import com.bots.RacoonShared.Discord.MessageLog;
import com.bots.RacoonShared.IncomingDataHandlers.BaseIncomingDataTrafficHandler;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IncomingMessageHandler extends BaseIncomingDataTrafficHandler {
    private final MessageOutput output;

    public IncomingMessageHandler(MessageOutput output) {
        this.output = output;
    }

    @Override
    public void handle(JSONObject data) {
        if (data.get("operation").equals("message")) {
            try {
                output.LogMessage((MessageLog) SerializationUtil.fromString(data.getString("body")));
            } catch (IOException | ClassNotFoundException e) {
                WindowLogger.getInstance().logError(
                        getClass().getName(),
                        e.getMessage()
                );
            }
        } else super.handle(data);
    }
}
