package com.bots.RaccoonClient.Views;

import com.bots.RaccoonClient.Communication.ConnectionSocketManager;
import com.bots.RaccoonClient.Events.ClientAuthorizedEvent.ClientAuthorizedSubscriber;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonClient.Views.Login.LoginViewController;
import com.bots.RaccoonClient.Views.Main.LogOutput;
import com.bots.RaccoonClient.Views.Main.MainViewController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ViewManager implements ClientAuthorizedSubscriber {

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
        ConnectionSocketManager.getInstance().getClientAuthorizedEventPublisher().subscribe(this);
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
            public void windowClosing(WindowEvent e) {
                ConnectionSocketManager.getInstance().disconnect();
                System.exit(0);
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

    @Override
    public void onClientAuthorization() {
        changeViewTo(View.MAIN);
    }
}
