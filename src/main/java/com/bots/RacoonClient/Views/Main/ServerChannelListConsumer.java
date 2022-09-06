package com.bots.RacoonClient.Views.Main;

import com.bots.RacoonShared.Discord.ServerChannels;
import java.util.List;

public interface ServerChannelListConsumer {
    void consumeServerChannelList(List<ServerChannels> content);
}
