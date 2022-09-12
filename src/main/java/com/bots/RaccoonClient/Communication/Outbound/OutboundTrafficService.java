package com.bots.RaccoonClient.Communication.Outbound;

import com.bots.RaccoonClient.Communication.TrafficManager;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonShared.Discord.BotMessage;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RaccoonShared.Util.SerializationUtil;
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
