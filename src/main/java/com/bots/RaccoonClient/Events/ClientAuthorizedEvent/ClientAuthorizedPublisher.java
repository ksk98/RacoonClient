package com.bots.RaccoonClient.Events.ClientAuthorizedEvent;


import com.bots.RaccoonShared.Events.Abstractions.GenericPublisher;

public class ClientAuthorizedPublisher extends GenericPublisher<ClientAuthorizedSubscriber> {
    public void notifySubscribers() {
        for (ClientAuthorizedSubscriber subscriber: subscribers)
            subscriber.onClientAuthorization();
    }
}
