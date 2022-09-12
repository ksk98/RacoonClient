package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Communication.Outbound.OutboundTrafficService;
import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingLogHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingMessageHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingServerChannelListHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.SSLFinishHandler;
import com.bots.RaccoonClient.Exceptions.SocketFactoryFailureException;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonClient.Views.Main.MainViewController;
import com.bots.RaccoonClient.Views.Main.MessageOutput;
import com.bots.RaccoonShared.IncomingDataHandlers.IncomingDataTrafficHandler;
import com.bots.RaccoonShared.SocketCommunication.CommunicationUtil;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RaccoonClient.Views.ViewManager;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import org.json.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.SocketTimeoutException;

public class ConnectionSocketManager {
    private static ConnectionSocketManager instance = null;

    private SSLSocket socket = null;
    private boolean loggedIn = false;

    private BufferedWriter out = null;
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
            ViewManager.displayError("Connection timed out.", "Connection timeout");
            disconnect();
            return;
        }

        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new DataInputStream(socket.getInputStream());
        trafficManager = new TrafficManager(socket, WindowLogger.getInstance(), createHandlerChain());
        trafficManager.start();

        OutboundTrafficService.getInstance().setTrafficManager(trafficManager);
    }

    public void disconnect() throws IOException {
        if (!isDisconnected())
            CommunicationUtil.sendTo(out, new JSONObject().put("operation", SocketOperationIdentifiers.CLIENT_DISCONNECT));

        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        if (trafficManager != null) trafficManager.stopRunning();

        loggedIn = false;
        socket = null;
    }

    public void login(String username, String password) {
        if (isDisconnected() || isLoggedIn())
            return;

        JSONObject loginJSON = new JSONObject()
                .put("operation", SocketOperationIdentifiers.CLIENT_LOGIN)
                .put("username", username)
                .put("password", password);

        SocketCommunicationOperationBuilder builder = new SocketCommunicationOperationBuilder();
        trafficManager.queueOperation(builder
                .setData(loginJSON)
                .setOnResponseReceived(response -> {
                    int responseCode = response.getInt("response_code");
                    if (responseCode == 200 || responseCode == 204) {
                        ViewManager.getInstance().changeViewTo(ViewManager.View.MAIN);

                        builder.clear();
                        builder
                                .setData(new JSONObject().put("operation", SocketOperationIdentifiers.REQUEST_SERVER_CHANNEL_LIST))
                                .setWaitForResponse(false);
                        trafficManager.queueOperation(builder.build());
                    } else {
                        try {
                            ViewManager.displayError(response.getString("message"), "Login failed");
                        } catch (JSONException e) {
                            ViewManager.displayError("Could not login.", "Login failed");
                        }

                        try {
                            disconnect();
                        } catch (IOException e) {
                            ViewManager.displayError(
                                    "Could not disconnect properly.", "Disconnect failed.");
                        }
                    }
                }).setOnErrorEncountered(ViewManager::displayError)
                .build());
    }

    private IncomingDataTrafficHandler createHandlerChain() {
        MainViewController mainViewController = (MainViewController) ViewManager.getInstance().getController(ViewManager.View.MAIN);

        IncomingDataTrafficHandler chain = new IncomingMessageHandler(new MessageOutput(mainViewController));
        chain   .setNext(new IncomingLogHandler())
                .setNext(new IncomingServerChannelListHandler(mainViewController))
                .setNext(new SSLFinishHandler());

        return chain;
    }

    public boolean isDisconnected() {
        return trafficManager == null || trafficManager.getSocketConnection().isClosed();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
