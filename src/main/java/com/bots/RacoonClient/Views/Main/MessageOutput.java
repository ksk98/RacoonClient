package com.bots.RacoonClient.Views.Main;

import com.bots.RacoonClient.Config;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.Discord.MessageLog;

import javax.swing.text.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MessageOutput {
    private final MainWindowController mainWindowController;
    private StyledDocument currentDocument = null;
    private final Map<Entry<String, String>, MutableAttributeSet> userAttributes;
    private final MutableAttributeSet messageAttribute, botAttribute;

    public MessageOutput(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        userAttributes = new HashMap<>();
        messageAttribute = new SimpleAttributeSet();
//        StyleConstants.setLeftIndent(messageAttribute, 32);
//        StyleConstants.setAlignment(messageAttribute, StyleConstants.ALIGN_RIGHT);
        botAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(botAttribute, Color.WHITE);
        StyleConstants.setBackground(botAttribute, new Color(88,101,242,255));
    }

    public void LogMessage(MessageLog message) {
        selectDocument(message.serverId, message.channelId);

        Entry<String, String> userAttributeKey = Map.entry(message.serverId, message.username);
        MutableAttributeSet userAttribute = userAttributes.get(userAttributeKey);
        if (userAttribute == null) {
            userAttribute = new SimpleAttributeSet();
            if (message.userColor != null)
                StyleConstants.setForeground(userAttribute, message.userColor);
            else {
                WindowLogger.getInstance().logInfo(
                        getClass().getName(),
                        "Received user color null in logged message. Defaulting to black..."
                );
                StyleConstants.setForeground(userAttribute, Color.BLACK);
            }
            StyleConstants.setBold(userAttribute, true);
            userAttributes.put(userAttributeKey, userAttribute);
        }

        try {
            append(Config.logTimestampFormat.format(ZonedDateTime.now().withZoneSameInstant(Config.zoneId)), messageAttribute);

            if (message.userIsBot()) {
                append("[BOT]", botAttribute);
                append(" ", messageAttribute);
            }
            append(message.username + ": ", userAttribute);
            append(message.message, messageAttribute);
            if (message.hasEmbeds()) append(" /embeded media/", messageAttribute);
            append("\n", messageAttribute);
        } catch (BadLocationException e) {
            WindowLogger.getInstance().logError(getClass().getName(), e.toString());
        }
    }

    private void selectDocument(String serverId, String channelId) {
        currentDocument = mainWindowController.getDocumentFor(serverId, channelId);
    }

    private void append(String message, AttributeSet attributes) throws BadLocationException {
        if (currentDocument == null) return;

        currentDocument.insertString(
                currentDocument.getLength(),
                message,
                attributes
        );
    }
}
