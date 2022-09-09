package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RacoonShared.Util.SerializationUtil;
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
