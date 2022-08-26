package com.bots.RacoonClient;

import com.bots.RacoonClient.Forms.LoginWindow;
import com.bots.RacoonClient.Forms.MainWindow;

import javax.swing.*;
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
        views.put(View.MAIN, new MainWindow(Config.windowTitle));
        currentView = View.LOGIN;
        getCurrentView().setVisible(true);
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
