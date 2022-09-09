package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RacoonShared.SocketCommunication.SocketOperationIdentifiers;
import org.json.JSONObject;

public class SSLFinishHandler extends IncomingOperationHandler {

    public SSLFinishHandler() {
        super(SocketOperationIdentifiers.SSL_HANDSHAKE_COMPLETE);
    }

    @Override
    public void consume(JSONObject jsonObject) {
        WindowLogger.getInstance().logSuccess(getClass().getName(), "SSL handshake completed.");
    }
}
