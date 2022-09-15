package com.bots.RaccoonClient.Events.TrafficManagerInstantiatedEvent;

import com.bots.RaccoonClient.Communication.TrafficManager;

public interface TrafficManagerStateSubscriber {
    void onTrafficManagerInstantiated(TrafficManager trafficManager);
    void onTrafficManagerDestroyed();
}
