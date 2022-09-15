package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonShared.Discord.BotMessage;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperation;

public interface ISocketOperationQueueingService {
    void queueOperation(SocketCommunicationOperation operation);
    void queueOperation(BotMessage message);
}
