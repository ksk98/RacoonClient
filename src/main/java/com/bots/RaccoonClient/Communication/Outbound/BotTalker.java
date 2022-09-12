package com.bots.RaccoonClient.Communication.Outbound;

import com.bots.RaccoonShared.Discord.BotMessage;

public interface BotTalker {
    void sendMessage(BotMessage message);
}
