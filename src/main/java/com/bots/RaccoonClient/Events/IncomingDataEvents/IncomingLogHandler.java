package com.bots.RaccoonClient.Events.IncomingDataEvents;

import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RaccoonShared.Logging.Log;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RaccoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IncomingLogHandler extends IncomingOperationHandler {

    public IncomingLogHandler() {
        super(SocketOperationIdentifiers.LOG_SERVER_LOG_TO_CLIENT);
    }

    @Override
    public void consume(JSONObject data) {
        try {
            WindowLogger.getInstance().logRemote((Log) SerializationUtil.fromString(data.getString("body")));
        } catch (IOException | ClassNotFoundException e) {
            WindowLogger.getInstance().logError(getClass().getName(), e.toString());
        }
    }
}
