package com.bots.RacoonClient.Events.IncomingDataEvents.Abstractions;

import com.bots.RacoonClient.WindowLogger;
import org.json.JSONObject;

public abstract class BaseIncomingDataTrafficHandler implements IncomingDataTrafficHandler {
    private IncomingDataTrafficHandler next = null;

    @Override
    public void setNext(IncomingDataTrafficHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(JSONObject response) {
        if (next != null)
            next.handle(response);
        else WindowLogger.getInstance().logInfo("Data reached the end of traffic handler chain and could not be handled.");
    }
}
