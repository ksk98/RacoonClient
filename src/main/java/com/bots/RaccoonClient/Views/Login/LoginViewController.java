package com.bots.RaccoonClient.Views.Login;

import com.bots.RaccoonClient.Communication.ConnectionSocketManager;
import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Exceptions.SocketFactoryFailureException;
import com.bots.RaccoonClient.Views.BaseViewController;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class LoginViewController extends BaseViewController {
    private final LoginView view;

    public LoginViewController() {
        super(new LoginView(Config.windowTitle));
        this.view = (LoginView) super.getView();
        addListeners();
    }

    private void addListeners() {
        KeyListener loginOnEnter = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    login();
            }
        };

        view.getURLField().addKeyListener(loginOnEnter);
        view.getPortField().addKeyListener(loginOnEnter);
        view.getUsernameField().addKeyListener(loginOnEnter);
        view.getPasswordField().addKeyListener(loginOnEnter);

        view.getLoginButton().addActionListener(event -> login());
    }

    private void login() {
        ConnectionSocketManager connectionSocketManager = ConnectionSocketManager.getInstance();
        int port;

        try {
            port = Integer.parseInt(view.getPortField().getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Invalid port.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (connectionSocketManager.isDisconnected())
                connectionSocketManager.connectTo(view.getURLField().getText(), port);

            connectionSocketManager.login(
                    view.getUsernameField().getText(),
                    String.valueOf(view.getPasswordField().getPassword())
            );
        } catch (UnknownHostException e) {
            showError("Unknown address: " + e, "Unknown address");
        } catch (ConnectException e) {
            showError("Could not connect to address.", "Connection error");
        } catch (IOException | SocketFactoryFailureException e) {
            showError(e.toString());
        }
    }

    public LoginView getView() {
        return view;
    }
}
