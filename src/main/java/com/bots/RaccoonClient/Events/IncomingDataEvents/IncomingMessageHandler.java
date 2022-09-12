package com.bots.RaccoonClient.Events.IncomingDataEvents;

import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonClient.Views.Main.MessageOutput;
import com.bots.RaccoonShared.Discord.MessageLog;
import com.bots.RaccoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RaccoonShared.Util.SerializationUtil;
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
