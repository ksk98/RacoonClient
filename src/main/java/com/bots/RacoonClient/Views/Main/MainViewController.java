package com.bots.RacoonClient.Views.Main;

import com.bots.RacoonClient.Config;
import com.bots.RacoonClient.Views.BaseViewController;
import com.bots.RacoonShared.Discord.Channel;
import com.bots.RacoonShared.Discord.ServerChannels;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class MainViewController extends BaseViewController implements ServerChannelListConsumer {
    private final MainView view;
    private final JComboBox<ServerChannels> serverPicker;
    private final JComboBox<Channel> channelPicker;
    private final Map<Entry<String, String>, StyledDocument> serverChannelDocument;
    private final Map<String, ServerChannels> serverChannelsMap;

    public MainViewController() {
        super(new MainView(Config.windowTitle));
        this.view = (MainView) super.getView();
        this.serverChannelDocument = new HashMap<>();
        this.serverChannelsMap = new HashMap<>();
        this.serverPicker = view.getMessagesTabServerPickBox();
        this.channelPicker = view.getMessagesTabChannelPickBox();
        addListeners();
    }

    private void addListeners() {
        this.serverPicker.addItemListener(e -> {
            refreshForNewServerSelection();
        });

        this.channelPicker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                serverChannelsMap.get(getSelectedServerId()).setLastSelectedChannelIndex(channelPicker.getSelectedIndex());
                view.getMessagesContentPane().setDocument(getDocumentForCurrent());
            }
        });
    }

    @Override
    public void consumeServerChannelList(List<ServerChannels> content) {
        serverPicker.removeAll();
        content.forEach(sc -> {
            serverChannelsMap.put(sc.serverId, sc);
            serverPicker.addItem(sc);
        });

        refreshForNewServerSelection();
    }

    public StyledDocument getDocumentFor(String serverId, String channelId) {
        Entry<String, String> key = Map.entry(serverId, channelId);
        if (!serverChannelDocument.containsKey(key))
            serverChannelDocument.put(key, new DefaultStyledDocument());

        return serverChannelDocument.get(key);
    }

    private void refreshForNewServerSelection() {
        if (serverPicker.getSelectedItem() == null)
            return;

        String serverId = ((ServerChannels) serverPicker.getSelectedItem()).serverId;
        if (!serverChannelsMap.containsKey(serverId))
            return;

        ServerChannels serverChannels = serverChannelsMap.get(serverId);
        channelPicker.removeAllItems();

        serverChannels.channels.forEach(channelPicker::addItem);

        if (channelPicker.getItemCount() > 0) {
            // If channel list changed this may not select the right last picked channel
            // However, the index should be reset if the list of channels is changed and
            // this is not the place to do this anyway
            try {
                channelPicker.setSelectedIndex(serverChannelsMap.get(getSelectedServerId()).getLastSelectedChannelIndex());
            } catch (IllegalArgumentException ignored) {
                // Could happen if channel list shrunk and last selected index is now out of bounds
                channelPicker.setSelectedIndex(0);
            }
        }

        view.getMessagesContentPane().setDocument(getDocumentFor(getSelectedServerId(), getSelectedChannelId()));
    }

    private StyledDocument getDocumentForCurrent() {
        return getDocumentFor(getSelectedServerId(), getSelectedChannelId());
    }

    private String getSelectedServerId() {
        try {
            return ((ServerChannels) Objects.requireNonNull(serverPicker.getSelectedItem())).serverId;
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    private String getSelectedChannelId() {
        try {
            return ((Channel) Objects.requireNonNull(channelPicker.getSelectedItem())).channelId();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public MainView getView() {
        return view;
    }
}
