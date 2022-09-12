package com.bots.RacoonClient.Communication.Outbound;

import com.bots.RacoonClient.Communication.TrafficManager;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.Discord.BotMessage;
import com.bots.RacoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RacoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;

public class OutboundTrafficService implements BotTalker {
    private static OutboundTrafficService instance = null;
    private TrafficManager trafficManager = null;

    public static OutboundTrafficService getInstance() {
        if (instance == null)
            instance = new OutboundTrafficService();

        return instance;
    }

    @Override
    public void sendMessage(BotMessage message) {
        if (trafficManager == null)
            return;

        JSONObject data = new JSONObject().put("operation", SocketOperationIdentifiers.SEND_MESSAGE_AS_BOT);
        try {
            data.put("body", SerializationUtil.toString(message));
        } catch (IOException e) {
            WindowLogger.getInstance().logError(getClass().getName(), "Could not serialize sent message: " + e);
            return;
        }

        SocketCommunicationOperationBuilder builder = new SocketCommunicationOperationBuilder();
        builder.setData(data)
                .setOnErrorEncountered(error ->
                        WindowLogger.getInstance().logError(
                                getClass().getName(), "Could not send message as bot: " + error
                        ))
                .setWaitForResponse(false);

        trafficManager.queueOperation(builder.build());
    }

    public void setTrafficManager(TrafficManager trafficManager) {
        this.trafficManager = trafficManager;
    }
}
