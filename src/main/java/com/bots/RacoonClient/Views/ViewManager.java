package com.bots.RacoonClient.Views;

import com.bots.RacoonClient.Communication.ConnectionSocketManager;
import com.bots.RacoonClient.Config;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Login.LoginView;
import com.bots.RacoonClient.Views.Login.LoginViewController;
import com.bots.RacoonClient.Views.Main.LogOutput;
import com.bots.RacoonClient.Views.Main.MainView;
import com.bots.RacoonClient.Views.Main.MainViewController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
    public enum View {
        LOGIN, MAIN
    }

    private static ViewManager instance = null;
    private final Map<View, BaseViewController> controllers = new HashMap<>();
    private View currentView;

    private ViewManager() {
        createViews();
        currentView = View.LOGIN;
        getCurrentView().setVisible(true);
    }

    public static ViewManager getInstance() {
        if (instance == null)
            instance = new ViewManager();

        return instance;
    }

    public void changeViewTo(View view) {
        getCurrentView().setVisible(false);
        currentView = view;
        getCurrentView().setVisible(true);
    }

    private void createViews() {
        LoginViewController loginViewController = new LoginViewController();
        MainViewController mainViewController = new MainViewController();

        controllers.put(View.LOGIN, loginViewController);
        controllers.put(View.MAIN, mainViewController);
        WindowLogger.getInstance().setTarget(new LogOutput(mainViewController.getView().getLogsContentPane()));

        getView(View.MAIN).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // TODO: doesn't work
                try {
                    ConnectionSocketManager.getInstance().disconnect();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    public static void displayError(String message) {
        getInstance().getCurrentController().showError(message);
    }

    public static void displayError(String message, String title) {
        getInstance().getCurrentController().showError(message, title);
    }

    public static void displayMessage(String message) {
        getInstance().getCurrentController().showMessage(message);
    }

    public static void displayMessage(String message, String title) {
        getInstance().getCurrentController().showMessage(message, title);
    }

    public BaseViewController getCurrentController() {
        return getController(currentView);
    }

    public BaseViewController getController(View view) {
        return controllers.get(view);
    }

    public JFrame getCurrentView() {
        return getView(currentView);
    }

    public JFrame getView(View view) {
        return getController(view).getView();
    }
}
