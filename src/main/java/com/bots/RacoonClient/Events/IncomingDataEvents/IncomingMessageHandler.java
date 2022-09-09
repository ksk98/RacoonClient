package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Main.MessageOutput;
import com.bots.RacoonShared.Discord.MessageLog;
import com.bots.RacoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RacoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IncomingMessageHandler extends IncomingOperationHandler {
    private final MessageOutput output;

    public IncomingMessageHandler(MessageOutput output) {
        super(SocketOperationIdentifiers.LOG_MESSAGE_TO_CLIENT);
        this.output = output;
    }

    @Override
    public void consume(JSONObject data) {
        try {
            output.LogMessage((MessageLog) SerializationUtil.fromString(data.getString("body")));
        } catch (IOException | ClassNotFoundException e) {
            WindowLogger.getInstance().logError(getClass().getName(), e.toString());
        }
    }
}
