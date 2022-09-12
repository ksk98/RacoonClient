package com.bots.RaccoonClient.Events.IncomingDataEvents;

import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
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
