package com.bots.RaccoonClient.Views.Main;

import com.bots.RaccoonClient.Communication.Outbound.OutboundTrafficService;
import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Views.BaseViewController;
import com.bots.RaccoonShared.Discord.BotMessage;
import com.bots.RaccoonShared.Discord.Channel;
import com.bots.RaccoonShared.Discord.ServerChannels;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
        serverPicker.addItemListener(e -> {
            refreshForNewServerSelection();
        });

        channelPicker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                serverChannelsMap.get(getSelectedServerId()).setLastSelectedChannelIndex(channelPicker.getSelectedIndex());
                view.getMessagesContentPane().setDocument(getDocumentForCurrent());
            }
        });

        view.getSendMessageContentPane().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown())
                        view.getSendMessageContentPane().append("\n");
                    else
                        sendMessage();
                }
            }
        });

        view.getSendMessageButton().addActionListener(e -> {
            sendMessage();
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

    private void sendMessage() {
        String message = view.getSendMessageContentPane().getText();
        if (Objects.requireNonNullElse(getSelectedServerId(), "").equals("") ||
                Objects.requireNonNullElse(getSelectedChannelId(), "").equals("") || message.equals(""))
            return;

        OutboundTrafficService.getInstance().sendMessage(
                new BotMessage(getSelectedServerId(), getSelectedChannelId(), message));

        view.getSendMessageContentPane().setText("");
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
