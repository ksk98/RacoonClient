package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Main.ServerChannelListConsumer;
import com.bots.RacoonShared.Discord.ServerChannels;
import com.bots.RacoonShared.IncomingDataHandlers.IncomingOperationHandler;
import com.bots.RacoonShared.SocketCommunication.SocketOperationIdentifiers;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class IncomingServerChannelListHandler extends IncomingOperationHandler {
    private final ServerChannelListConsumer serverChannelListConsumer;

    public IncomingServerChannelListHandler(ServerChannelListConsumer serverChannelListConsumer) {
        super(SocketOperationIdentifiers.UPDATE_SERVER_CHANNEL_LIST);
        this.serverChannelListConsumer = serverChannelListConsumer;
    }

    @Override
    @SuppressWarnings("unchecked")  // No idea how to deal with this warning
    public void consume(JSONObject data) {
        try {
            serverChannelListConsumer.consumeServerChannelList(
                    (List<ServerChannels>) SerializationUtil.fromString(data.getString("body"))
            );
        } catch (IOException | ClassNotFoundException e) {
            WindowLogger.getInstance().logError(getClass().getName(), e.toString());
        }
    }
}
