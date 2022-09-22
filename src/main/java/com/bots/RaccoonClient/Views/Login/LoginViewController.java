package com.bots.RaccoonClient.Views.Login;

import com.bots.RaccoonClient.CacheFilesManager;
import com.bots.RaccoonClient.Communication.ConnectionSocketManager;
import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Events.ClientAuthorizedEvent.ClientAuthorizedSubscriber;
import com.bots.RaccoonClient.Exceptions.CommunicationEstablishException;
import com.bots.RaccoonClient.Views.BaseViewController;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginViewController extends BaseViewController implements ClientAuthorizedSubscriber {
    private final LoginView view;

    public LoginViewController() {
        super(new LoginView(Config.windowTitle));
        this.view = (LoginView) super.getView();
        ConnectionSocketManager.getInstance().getClientAuthorizedEventPublisher().subscribe(this);
        addListeners();
        readLoginCache();
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
        int port;
        try {port = Integer.parseInt(getPort());}
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Invalid port.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ConnectionSocketManager connectionSocketManager = ConnectionSocketManager.getInstance();

        try {connectionSocketManager.establishCommunication(getURL(), port, getUsername(), getPassword());}
        catch (CommunicationEstablishException e) {
            showError(e.toString(), "Login failed");
        }
    }

    private void readLoginCache() {
        JSONObject cache = CacheFilesManager.getCacheIfExists(CacheFilesManager.loginCacheFilePath);
        if (cache == null)
            return;

        view.getURLField().setText(cache.getString("url"));
        view.getPortField().setText(cache.getString("port"));
        view.getUsernameField().setText(cache.getString("username"));
        view.getPasswordField().requestFocus();
    }

    private void writeLoginCache() {
        JSONObject content = new JSONObject();
        content.put("url", view.getURLField().getText())
                .put("port", view.getPortField().getText())
                .put("username", view.getUsernameField().getText());

        CacheFilesManager.writeCache(CacheFilesManager.loginCacheFilePath, content);
    }

    public LoginView getView() {
        return view;
    }

    private String getURL() {
        return view.getURLField().getText();
    }

    private String getPort() {
        return view.getPortField().getText();
    }

    private String getUsername() {
        return view.getUsernameField().getText();
    }

    private String getPassword() {
        return String.valueOf(view.getPasswordField().getPassword());
    }

    @Override
    public void onClientAuthorization() {
        if (view.getRememberMeCheckBox().isSelected())
            writeLoginCache();
    }
}
