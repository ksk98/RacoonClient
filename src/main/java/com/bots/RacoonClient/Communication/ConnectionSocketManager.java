package com.bots.RacoonClient.Communication;

import com.bots.RacoonClient.Config;
import com.bots.RacoonClient.Events.IncomingDataEvents.IncomingLogHandler;
import com.bots.RacoonClient.Events.IncomingDataEvents.IncomingMessageHandler;
import com.bots.RacoonClient.Exceptions.SocketFactoryFailureException;
import com.bots.RacoonClient.Loggers.WindowLogger;
import com.bots.RacoonClient.Views.Main.MainWindow;
import com.bots.RacoonClient.Views.Main.MessageOutput;
import com.bots.RacoonShared.IncomingDataHandlers.IncomingDataTrafficHandler;
import com.bots.RacoonShared.SocketCommunication.CommunicationUtil;
import com.bots.RacoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RacoonClient.WindowManager;
import org.json.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.SocketTimeoutException;

public class ConnectionSocketManager {
    private static ConnectionSocketManager instance = null;

    private SSLSocket socket = null;
    private boolean loggedIn = false;

    private PrintWriter out = null;
    private DataInputStream in = null;
    private TrafficManager trafficManager = null;

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
            WindowManager.displayError("Connection timed out.", "Connection timeout");
            disconnect();
            return;
        }

        out = new PrintWriter(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        trafficManager = new TrafficManager(socket, WindowLogger.getInstance(), createHandlerChain());
        trafficManager.start();
    }

    public void disconnect() throws IOException {
        if (!isDisconnected())
            CommunicationUtil.sendTo(out, new JSONObject().put("operation", "disconnect"));

        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        if (trafficManager != null) trafficManager.stopRunning();

        loggedIn = false;
        socket = null;
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
                            WindowManager.displayError(response.getString("message"), "Login failed");
                        } catch (JSONException e) {
                            WindowManager.displayError("Could not login.", "Login failed");
                        }

                        try {
                            disconnect();
                        } catch (IOException e) {
                            WindowManager.displayError(
                                    "Could not disconnect properly.", "Disconnect failed.");
                        }
                    }
                }).setOnErrorEncountered(WindowManager::displayError)
                .build());
    }

    public boolean isDisconnected() {
        return trafficManager == null || trafficManager.getSocketConnection().isClosed();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private IncomingDataTrafficHandler createHandlerChain() {
        return new IncomingMessageHandler(
                new IncomingLogHandler(null),
                new MessageOutput(
                        ((MainWindow) WindowManager.getInstance().getView(WindowManager.View.MAIN))
                                .getMessagesContentPane())
        );
    }
}
