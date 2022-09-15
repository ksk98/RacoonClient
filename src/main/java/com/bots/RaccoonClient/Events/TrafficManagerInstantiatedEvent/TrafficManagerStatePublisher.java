package com.bots.RaccoonClient.Events.TrafficManagerInstantiatedEvent;

import com.bots.RaccoonClient.Communication.TrafficManager;
import com.bots.RaccoonShared.Events.Abstractions.GenericPublisher;

public class TrafficManagerStatePublisher extends GenericPublisher<TrafficManagerStateSubscriber> {
    public void notifySubscribersOnCreation(TrafficManager trafficManager) {
        for (TrafficManagerStateSubscriber subscriber: subscribers) {
            subscriber.onTrafficManagerInstantiated(trafficManager);
        }
    }

    public void notifySubscribersOnDestroy() {
        for (TrafficManagerStateSubscriber subscriber: subscribers) {
            subscriber.onTrafficManagerDestroyed();
        }
    }
}
