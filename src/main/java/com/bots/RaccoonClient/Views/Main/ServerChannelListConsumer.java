package com.bots.RaccoonClient.Views.Main;

import com.bots.RaccoonShared.Discord.ServerChannels;
import java.util.List;

public interface ServerChannelListConsumer {
    void consumeServerChannelList(List<ServerChannels> content);
}
