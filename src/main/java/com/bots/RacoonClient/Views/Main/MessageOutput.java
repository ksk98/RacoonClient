package com.bots.RacoonClient.Views.Main;

import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonShared.Discord.MessageLog;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MessageOutput {
    private final JTextPane textPane;
    private final Map<Entry<String, String>, MutableAttributeSet> userAttributes;
    private final MutableAttributeSet messageAttribute, botAttribute;

    public MessageOutput(JTextPane output) {
        this.textPane = output;
        userAttributes = new HashMap<>();
        messageAttribute = new SimpleAttributeSet();
        StyleConstants.setLeftIndent(messageAttribute, 32);
        StyleConstants.setAlignment(messageAttribute, StyleConstants.ALIGN_RIGHT);
        botAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(botAttribute, Color.WHITE);
        StyleConstants.setBackground(botAttribute, new Color(88,101,242,255));
    }

    public void LogMessage(MessageLog message) {
        Entry<String, String> userAttributeKey = Map.entry(message.serverId, message.username);
        MutableAttributeSet userAttribute = userAttributes.get(userAttributeKey);
        if (userAttribute == null) {
            userAttribute = new SimpleAttributeSet();
            StyleConstants.setForeground(userAttribute, message.userColor);
            userAttributes.put(userAttributeKey, userAttribute);
        }

        try {
            if (message.userIsBot()) {
                append("[BOT]", botAttribute);
                append(" ", messageAttribute);
            }
            append(message.username, userAttribute);
            append(message.message, messageAttribute);
            if (message.hasEmbeds()) append(" /embeded media/", messageAttribute);
            append("\n", messageAttribute);
        } catch (BadLocationException e) {
            WindowLogger.getInstance().logError(getClass().getName(), e.toString());
        }
    }

    private void append(String message, AttributeSet attributes) throws BadLocationException {
        textPane.getStyledDocument().insertString(
                textPane.getStyledDocument().getLength(),
                message,
                attributes
        );
    }
}