package com.bots.RacoonClient.Communication.Outbound;

import com.bots.RacoonShared.Discord.BotMessage;

public interface BotTalker {
    void sendMessage(BotMessage message);
}
