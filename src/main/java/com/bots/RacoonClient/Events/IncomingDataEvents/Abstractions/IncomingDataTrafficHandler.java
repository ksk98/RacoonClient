package com.bots.RacoonClient.Events.IncomingDataEvents.Abstractions;

import org.json.JSONObject;

public interface IncomingDataTrafficHandler {
    void setNext(IncomingDataTrafficHandler handler);

    /**
     * Handle a given response or pass it to the next available handler.
     * Response MUST contain a key "operation" to distinguish which handler should
     * make use of the response.
     */
    void handle(JSONObject response);
}
