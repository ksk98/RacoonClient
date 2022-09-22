package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Events.ClientAuthorizedEvent.ClientAuthorizedPublisher;
import com.bots.RaccoonClient.Events.ClientAuthorizedEvent.ClientAuthorizedSubscriber;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingLogHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingMessageHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.IncomingServerChannelListHandler;
import com.bots.RaccoonClient.Events.IncomingDataEvents.SSLFinishHandler;
import com.bots.RaccoonClient.Events.TrafficManagerInstantiatedEvent.TrafficManagerStatePublisher;
import com.bots.RaccoonClient.Events.TrafficManagerInstantiatedEvent.TrafficManagerStateSubscriber;
import com.bots.RaccoonClient.Exceptions.CommunicationEstablishException;
import com.bots.RaccoonClient.Exceptions.SocketConnectionCreationException;
import com.bots.RaccoonClient.Exceptions.SocketFactoryFailureException;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonClient.Views.Main.MainViewController;
import com.bots.RaccoonClient.Views.Main.MessageOutput;
import com.bots.RaccoonShared.Events.Abstractions.IGenericPublisher;
import com.bots.RaccoonShared.IncomingDataHandlers.IJSONDataHandler;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperationBuilder;
import com.bots.RaccoonClient.Views.ViewManager;
import com.bots.RaccoonShared.SocketCommunication.SocketOperationIdentifiers;
import org.json.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.SocketException;

public class ConnectionSocketManager {
    private static ConnectionSocketManager instance = null;

    public enum State {
        WAITING, CONNECTING, AUTHORIZING, ESTABLISHED, DISCONNECTING
    }
    private State currentState = State.WAITING;

    private final ClientAuthorizedPublisher clientAuthorizedPublisher;
    private final TrafficManagerStatePublisher trafficManagerStatePublisher;

    private SSLSocket socket = null;
    private TrafficManager trafficManager = null;

    private ConnectionSocketManager() {
        System.setProperty("javax.net.ssl.trustStore", Config.localKeystorePath);
        clientAuthorizedPublisher = new ClientAuthorizedPublisher();
        trafficManagerStatePublisher = new TrafficManagerStatePublisher();
    }

    public static ConnectionSocketManager getInstance() {
        if (instance == null)
            instance = new ConnectionSocketManager();

        return instance;
    }

    public void establishCommunication(String host, int port, String username, String password) throws CommunicationEstablishException {
        if (currentState != State.WAITING) return;

        setCurrentState(State.CONNECTING);

        try {
            socket = createConnection(host, port);
            setupTrafficManager();
        } catch (CommunicationEstablishException e) {
            clearCommunication();
            throw e;
        }
        WindowLogger.getInstance().logSuccess(getClass().getName(), "Connection established!");

        setCurrentState(State.AUTHORIZING);

        attemptToAuthorize(username, password);
    }

    private SSLSocket createConnection(String host, int port) throws CommunicationEstablishException {
        SSLSocketFactory factory;
        SSLSocket connection;
        try {factory = SSLUtil.getSocketFactory();}
        catch (SocketFactoryFailureException e) {
            throw new CommunicationEstablishException("Could not create SSLSocketFactory: " + e);
        }

        try {connection = (SSLSocket)factory.createSocket(host, port);}
        catch (IOException e) {
            throw new CommunicationEstablishException("Could not create SSLSocket: " + e);
        }

        try {connection.setSoTimeout(Config.SocketTimeoutMS);}
        catch (SocketException e) {
            throw new CommunicationEstablishException("Could not set timeout for socket: " + e);
        }

        return connection;
    }

    private void setupTrafficManager() throws CommunicationEstablishException {
        try {
            trafficManager = new TrafficManager(socket, WindowLogger.getInstance(), createHandlerChain());
        } catch (SocketConnectionCreationException e) {
            throw new CommunicationEstablishException("Could not create Traffic Manager: " + e);
        }

        trafficManager.start();
        trafficManagerStatePublisher.notifySubscribersOnCreation(trafficManager);
    }

    private void attemptToAuthorize(String username, String password) {
        JSONObject content = new JSONObject()
                .put("operation", SocketOperationIdentifiers.CLIENT_LOGIN)
                .put("username", username)
                .put("password", password);

        SocketCommunicationOperationBuilder builder = new SocketCommunicationOperationBuilder();
        builder.setData(content)
                .setOnResponseReceived(response -> {
                    int responseCode;

                    try {responseCode = response.getInt("response_code");}
                    catch (JSONException e) {
                        ViewManager.displayError("Authorization response yielded no response code.", "Authorization error.");
                        clearCommunication();
                        return;
                    }

                    if (responseCode == 204) {
                        setCurrentState(State.ESTABLISHED);
                    } else {
                        String message;

                        try {message = response.getString("message");}
                        catch (JSONException ignored) {
                            message = "No explanation provided.";
                        }

                        ViewManager.displayError("Authorization attempt rejected by server: " + message);
                        clearCommunication();
                    }
                })
                .setOnErrorEncountered(error -> {
                    ViewManager.displayError(error, "Authorization failed");
                    clearCommunication();
                });

        trafficManager.queueOperation(builder.build());
    }

    public void disconnect() {
        if (currentState != State.ESTABLISHED) return;

        setCurrentState(State.DISCONNECTING);
        clearCommunication();
    }

    private void clearTrafficManager() {
        trafficManager = null;
        trafficManagerStatePublisher.notifySubscribersOnDestroy();
    }

    private void clearCommunication() {
        socket = null;
        clearTrafficManager();
        setCurrentState(State.WAITING);
    }

    private IJSONDataHandler createHandlerChain() {
        MainViewController mainViewController = (MainViewController) ViewManager.getInstance().getController(ViewManager.View.MAIN);

        IJSONDataHandler chain = new IncomingMessageHandler(new MessageOutput(mainViewController));
        chain   .setNext(new IncomingLogHandler())
                .setNext(new IncomingServerChannelListHandler(mainViewController))
                .setNext(new SSLFinishHandler());

        return chain;
    }

    private void setCurrentState(State state) {
        this.currentState = state;

        if (state == State.ESTABLISHED)
            clientAuthorizedPublisher.notifySubscribers();
    }

    public IGenericPublisher<ClientAuthorizedSubscriber> getClientAuthorizedEventPublisher() {
        return clientAuthorizedPublisher;
    }

    public IGenericPublisher<TrafficManagerStateSubscriber> getTrafficManagerStatePublisher() {
        return trafficManagerStatePublisher;
    }
}
