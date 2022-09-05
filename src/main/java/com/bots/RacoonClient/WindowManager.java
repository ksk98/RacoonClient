package com.bots.RacoonClient;

import com.bots.RacoonClient.Communication.ConnectionSocketManager;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.LoginWindow;
import com.bots.RacoonClient.Views.Main.LogOutput;
import com.bots.RacoonClient.Views.Main.MainWindow;
import com.bots.RacoonClient.Views.Main.MainWindowController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WindowManager {
    public enum View {
        LOGIN, MAIN
    }

    private static WindowManager instance = null;
    private Map<View, JFrame> views;
    private final MainWindowController mainWindowController;
    private View currentView;

    private WindowManager() {
        createViews();
        currentView = View.LOGIN;
        getCurrentView().setVisible(true);
        mainWindowController = new MainWindowController((MainWindow) getView(View.MAIN));
    }

    public static WindowManager getInstance() {
        if (instance == null)
            instance = new WindowManager();

        return instance;
    }

    public void changeViewTo(View view) {
        getCurrentView().setVisible(false);
        currentView = view;
        getCurrentView().setVisible(true);
    }

    public JFrame getCurrentView() {
        return views.get(currentView);
    }

    public JFrame getView(View view) {
        return views.get(view);
    }

    public static void displayError(String message) {
        displayError(message, Config.windowTitle);
    }

    public static void displayError(String message, String title) {
        JOptionPane.showMessageDialog(getInstance().getCurrentView(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void displayMessage(String message) {
        displayMessage(message, Config.windowTitle);
    }

    public static void displayMessage(String message, String title) {
        JOptionPane.showMessageDialog(getInstance().getCurrentView(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void createViews() {
        views = new HashMap<>();

        views.put(View.LOGIN, new LoginWindow(Config.windowTitle));

        MainWindow mainWindow = new MainWindow(Config.windowTitle);
        WindowLogger.getInstance().setTarget(new LogOutput(mainWindow.getLogsContentPane()));
        views.put(View.MAIN, mainWindow);

        views.get(View.MAIN).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    ConnectionSocketManager.getInstance().disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }
}
