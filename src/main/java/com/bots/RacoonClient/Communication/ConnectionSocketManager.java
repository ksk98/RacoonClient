package com.bots.RacoonClient.Communication;

import com.bots.RacoonClient.Config;
import com.bots.RacoonClient.Events.IncomingDataEvents.IncomingLogHandler;
import com.bots.RacoonClient.Exceptions.SocketFactoryFailureException;
import com.bots.RacoonClient.WindowLogger;
import com.bots.RacoonShared.SocketCommunication.CommunicationUtil;
import com.bots.RacoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RacoonClient.WindowManager;
import org.json.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.SocketTimeoutException;

public class ConnectionSocketManager {
    private static ConnectionSocketManager instance = null;

    private SSLSocket socket = null;
    private boolean loggedIn = false;

    private PrintWriter out;
    private DataInputStream in;
    private TrafficManager trafficManager;

    private ConnectionSocketManager() {
        System.setProperty("javax.net.ssl.trustStore", Config.localKeystorePath);
    }

    public static ConnectionSocketManager getInstance() {
        if (instance == null)
            instance = new ConnectionSocketManager();

        return instance;
    }

    public void connectTo(String host, int port) throws IOException, SocketFactoryFailureException {
        SSLSocketFactory factory = SSLUtil.getSocketFactory();
        socket = (SSLSocket)factory.createSocket(host, port);
        socket.setSoTimeout(Config.SocketTimeoutMS);

        try {
            socket.startHandshake();
        } catch (SocketTimeoutException e) {
            JOptionPane.showMessageDialog(WindowManager.getInstance().getCurrentView(), "Socket timed out.");
            socket.close();
            return;
        }

        out = new PrintWriter(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        trafficManager = new TrafficManager(socket, WindowLogger.getInstance(), new IncomingLogHandler(null));
        trafficManager.start();
    }

    public void disconnect() throws IOException {
        if (!isDisconnected())
            CommunicationUtil.sendTo(out, new JSONObject().put("operation", "disconnect"));
        in.close();
        out.close();
        socket.close();
        trafficManager.stopRunning();
        loggedIn = false;
    }

    public void login(String username, String password) {
        if (isDisconnected())
            return;

        JSONObject loginJSON = new JSONObject()
                .put("operation", "login")
                .put("username", username)
                .put("password", password);

        SocketCommunicationOperationBuilder builder = new SocketCommunicationOperationBuilder();
        trafficManager.queueOperation(builder
                .setData(loginJSON)
                .setOnResponseReceived(response -> {
                    int responseCode = response.getInt("response_code");
                    if (responseCode == 200 || responseCode == 204) {
                        WindowManager.getInstance().changeViewTo(WindowManager.View.MAIN);
                    } else {
                        try {
                            displayErrorDialog(response.getString("message"), "Login failed");
                        } catch (JSONException e) {
                            displayErrorDialog("Could not login.", "Login failed");
                        }
                    }
                }).setOnErrorEncountered(this::displayErrorDialog)
                .build());
    }

    private void displayErrorDialog(String message) {
        displayErrorDialog(message, "Error");
    }

    private void displayErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(WindowManager.getInstance().getCurrentView(),
                message, title, JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean isDisconnected() {
        return trafficManager == null || trafficManager.getSocketConnection().isClosed();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
