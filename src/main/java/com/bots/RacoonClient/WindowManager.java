package com.bots.RacoonClient;

import com.bots.RacoonClient.Communication.ConnectionSocketManager;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.LoginWindow;
import com.bots.RacoonClient.Views.Main.LogOutput;
import com.bots.RacoonClient.Views.Main.MainWindow;

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
    private Map<View, JFrame> views = new HashMap<>();
    private View currentView;

    private WindowManager() {
        views.put(View.LOGIN, new LoginWindow(Config.windowTitle));

        MainWindow mainWindow = new MainWindow(Config.windowTitle);
        WindowLogger.getInstance().setTarget(new LogOutput(mainWindow.getLogsContentPane()));
        views.put(View.MAIN, mainWindow);

        currentView = View.LOGIN;
        getCurrentView().setVisible(true);

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
}
