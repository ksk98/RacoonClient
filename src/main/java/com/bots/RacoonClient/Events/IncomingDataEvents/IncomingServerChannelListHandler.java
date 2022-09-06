package com.bots.RacoonClient.Events.IncomingDataEvents;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Main.ServerChannelListConsumer;
import com.bots.RacoonShared.Discord.ServerChannels;
import com.bots.RacoonShared.IncomingDataHandlers.BaseIncomingDataTrafficHandler;
import com.bots.RacoonShared.Util.SerializationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class IncomingServerChannelListHandler extends BaseIncomingDataTrafficHandler {
    private final ServerChannelListConsumer serverChannelListConsumer;

    public IncomingServerChannelListHandler(ServerChannelListConsumer serverChannelListConsumer) {
        this.serverChannelListConsumer = serverChannelListConsumer;
    }

    @Override
    @SuppressWarnings("unchecked")  // No idea how to deal with this warning
    public void handle(JSONObject data) {
        if (data.get("operation").equals("setServerChannelList")) {
            try {
                serverChannelListConsumer.consumeServerChannelList(
                        (List<ServerChannels>) SerializationUtil.fromString(data.getString("body"))
                );
            } catch (IOException | ClassNotFoundException e) {
                WindowLogger.getInstance().logError(
                        getClass().getName(),
                        e.getMessage()
                );
            }
        } else super.handle(data);
    }
}
