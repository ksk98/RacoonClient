package com.bots.RacoonClient.Views.Main;

public record Channel(String channelId, String channelName) {
    @Override
    public String toString() {
        return channelName;
    }
}
