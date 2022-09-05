package com.bots.RacoonClient.Views.Main;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MainWindowController {
    private final MainWindow mainWindow;
    private final JComboBox<ServerChannels> serverPicker;
    private final JComboBox<Channel> channelPicker;
    private final Map<Entry<String, String>, StyledDocument> serverChannelDocument;
    private final Map<String, ServerChannels> serverChannelsMap;

    public MainWindowController(MainWindow mainWindow) {
        this.serverChannelDocument = new HashMap<>();
        serverChannelsMap = new HashMap<>();

        this.mainWindow = mainWindow;
        this.serverPicker = mainWindow.getMessagesTabServerPickBox();
        this.serverPicker.addItemListener(e -> {
            refreshForNewServerSelection();
        });
        this.channelPicker = mainWindow.getMessagesTabChannelPickBox();
        this.channelPicker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                serverChannelsMap.get(getSelectedServerId()).setLastSelectedChannelIndex(channelPicker.getSelectedIndex());
                mainWindow.getMessagesContentPane().setDocument(getDocumentForCurrent());
            }
        });
    }

    public void setServerChannels(List<ServerChannels> content) {
        serverPicker.removeAll();
        content.forEach(sc -> {
            serverChannelsMap.put(sc.serverId, sc);
            serverPicker.addItem(sc);
        });

        serverChannelDocument.clear();
        refreshForNewServerSelection();
    }

    private void refreshForNewServerSelection() {
        if (serverPicker.getSelectedItem() == null)
            return;

        String serverId = (String) serverPicker.getSelectedItem();
        if (!serverChannelsMap.containsKey(serverId))
            return;

        ServerChannels serverChannels = serverChannelsMap.get(serverId);
        channelPicker.removeAll();

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

        mainWindow.getMessagesContentPane().setDocument(getDocumentFor(getSelectedServerId(), getSelectedChannelId()));
    }

    private StyledDocument getDocumentForCurrent() {
        return getDocumentFor(getSelectedServerId(), getSelectedChannelId());
    }

    private StyledDocument getDocumentFor(String serverId, String channelId) {
        Entry<String, String> key = Map.entry(serverId, channelId);
        if (!serverChannelDocument.containsKey(key))
            serverChannelDocument.put(key, new DefaultStyledDocument());

        return serverChannelDocument.get(key);
    }

    private String getSelectedServerId() {
        return (String) serverPicker.getSelectedItem();
    }

    private String getSelectedChannelId() {
        return (String) channelPicker.getSelectedItem();
    }
}
