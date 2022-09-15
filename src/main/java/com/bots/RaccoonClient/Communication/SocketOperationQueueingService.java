package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Events.TrafficManagerInstantiatedEvent.TrafficManagerStateSubscriber;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonShared.Discord.BotMessage;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperation;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RaccoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class SocketOperationQueueingService implements TrafficManagerStateSubscriber, ISocketOperationQueueingService {
    private static SocketOperationQueueingService instance = null;
    private TrafficManager trafficManager = null;
    private final Queue<SocketCommunicationOperation> operationStorage = new LinkedList<>();

    private SocketOperationQueueingService() {
        ConnectionSocketManager.getInstance().getTrafficManagerStatePublisher().subscribe(this);
    }

    public static ISocketOperationQueueingService getInstance() {
        if (instance == null)
            instance = new SocketOperationQueueingService();

        return instance;
    }

    @Override
    public void queueOperation(SocketCommunicationOperation operation) {
        if (trafficManager != null)
            trafficManager.queueOperation(operation);
        else operationStorage.add(operation);
    }

    @Override
    public void queueOperation(BotMessage message) {
        JSONObject data = new JSONObject().put("operation", SocketOperationIdentifiers.SEND_MESSAGE_AS_BOT);

        try {data.put("body", SerializationUtil.toString(message));}
        catch (IOException e) {
            WindowLogger.getInstance().logError(getClass().getName(), "Could not serialize sent message: " + e);
            return;
        }

        SocketCommunicationOperationBuilder builder = new SocketCommunicationOperationBuilder();
        builder.setData(data)
                .setOnErrorEncountered(error ->
                        WindowLogger.getInstance().logError(
                                getClass().getName(), "Could not send message as bot: " + error
                        ));

        queueOperation(builder.build());
    }

    @Override
    public void onTrafficManagerInstantiated(TrafficManager trafficManager) {
        this.trafficManager = trafficManager;

        SocketCommunicationOperation operation;
        while ((operation = operationStorage.poll()) != null)
            trafficManager.queueOperation(operation);
    }

    @Override
    public void onTrafficManagerDestroyed() {
        this.trafficManager = null;
    }
}
