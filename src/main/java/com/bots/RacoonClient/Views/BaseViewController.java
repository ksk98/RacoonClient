package com.bots.RacoonClient.Views;

import com.bots.RacoonClient.Config;

import javax.swing.*;

public abstract class BaseViewController {
    protected final JFrame view;

    public BaseViewController(JFrame view) {
        this.view = view;
    }

    public final void showError(String message) {
        showError(message, "Error");
    }

    public final void showError(String message, String title) {
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public final void showMessage(String message) {
        showMessage(message, Config.windowTitle);
    }

    public final void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public JFrame getView() {
        return view;
    }
}
