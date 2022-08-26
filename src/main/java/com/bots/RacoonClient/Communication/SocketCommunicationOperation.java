package com.bots.RacoonClient.Communication;

import com.bots.RacoonClient.WindowLogger;
import org.json.JSONObject;

import java.util.function.Consumer;

public class SocketCommunicationOperation {
    public final JSONObject request;
    public final Consumer<JSONObject> onResponse;
    public final Consumer<String> onError;

    public SocketCommunicationOperation(JSONObject request, Consumer<JSONObject> onResponse) {
        this(request, onResponse, error -> WindowLogger.getInstance().logError(error));
    }

    public SocketCommunicationOperation(JSONObject request, Consumer<JSONObject> onResponse, Consumer<String> onOutStreamError) {
        this.request = request;
        this.onResponse = onResponse;
        this.onError = onOutStreamError;
    }
}
