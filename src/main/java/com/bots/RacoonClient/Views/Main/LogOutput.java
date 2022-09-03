package com.bots.RacoonClient.Views.Main;

import com.bots.RacoonShared.Logging.Log;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class LogOutput {
    private final JTextPane textPane;
    private final MutableAttributeSet localCallerAttribute, remoteCallerAttribute, messageAttribute;

    public LogOutput(JTextPane pane) {
        this.textPane = pane;
        this.localCallerAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(this.localCallerAttribute, new Color(14, 133, 178));
        this.remoteCallerAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(this.remoteCallerAttribute, new Color(75, 173, 10));
        this.messageAttribute = new SimpleAttributeSet();
    }

    public void outputRemoteLog(Log log) {
        try {
            textPane.getStyledDocument().insertString(
                    textPane.getStyledDocument().getLength(),
                    "<SERVER> " + log.getCaller() + ": ",
                    localCallerAttribute
            );
            textPane.getStyledDocument().insertString(
                    textPane.getStyledDocument().getLength(),
                    log.getMessage() + "\n",
                    messageAttribute
            );
        } catch (BadLocationException e) {
            // Can't really try to log it out to the window anymore...
            // Should not happen though
            e.printStackTrace();
        }
    }

    public void outputLocalLog(Log log) {
        try {
            textPane.getStyledDocument().insertString(
                    textPane.getStyledDocument().getLength(),
                    "<LOCAL>" + log.getCaller() + ": ",
                    localCallerAttribute
            );
            textPane.getStyledDocument().insertString(
                    textPane.getStyledDocument().getLength(),
                    log.getMessage() + "\n",
                    messageAttribute
            );
        } catch (BadLocationException e) {
            // Can't really try to log it out to the window anymore...
            // Should not happen though
            e.printStackTrace();
        }
    }
}
