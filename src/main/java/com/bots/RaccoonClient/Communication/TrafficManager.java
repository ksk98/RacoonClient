package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Exceptions.SocketConnectionCreationException;
import com.bots.RaccoonClient.Loggers.WindowLogger;
import com.bots.RaccoonClient.Views.ViewManager;
import com.bots.RaccoonShared.IncomingDataHandlers.IJSONDataHandler;
import com.bots.RaccoonShared.Logging.Loggers.Logger;
import com.bots.RaccoonShared.SocketCommunication.CommunicationUtil;
import com.bots.RaccoonShared.SocketCommunication.SocketCommunicationOperation;
import org.json.JSONObject;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/***
 * Class that is used to manage and execute inbound and outbound traffic.
 */
public class TrafficManager extends Thread {
    private boolean running = false;
    private final SocketConnection socketConnection;
    private final Logger logger;

    private final Map<Integer, SocketCommunicationOperation> operations;
    private final Queue<Integer> idToSendQueue;
    private int nextId;

    private final IJSONDataHandler incomingDataHandler;

    /***
     * @param socket socket which traffic will be going through
     * @param logger used to log necessary information
     * @param handlerChain chain of responsibility that utilises IncomingDataTrafficHandler interface, used in
     *                     handling incoming traffic that is not part of any queued operations
     */
    public TrafficManager(SSLSocket socket, Logger logger, IJSONDataHandler handlerChain) throws SocketConnectionCreationException {
        this.socketConnection = new SocketConnection(socket);
        this.logger = logger;

        this.operations = new HashMap<>();
        this.idToSendQueue = new LinkedList<>();
        this.nextId = 0;

        incomingDataHandler = handlerChain;
    }

    public void stopRunning() {
        running = false;
    }

    public void queueOperation(SocketCommunicationOperation operation) {
        operations.put(nextId, operation);
        idToSendQueue.add(nextId);
        nextId += 1;
    }

    private void finaliseOperationForResponse(JSONObject response) {
        int id = response.getInt("client_operation_id");
        SocketCommunicationOperation operation = operations.remove(id);
        operation.getOnResponseReceived().accept(response);

        if (operations.isEmpty())
            nextId = 0;
    }

    private void removeOperation(int id) {
        operations.remove(id);

        if (operations.isEmpty())
            nextId = 0;
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            if (socketConnection.isClosed()) {
                try {
                    socketConnection.socket.close();
                    ViewManager.displayError(
                            "Connection with host has been lost.", "Connection was lost");
                } catch (IOException e) {
                    ViewManager.displayError(
                            "Connection with host has been lost and the socket could not be closed.",
                            "Connection was lost"
                    );
                }
                running = false;
                break;
            }

            if (!idToSendQueue.isEmpty()) {
                Integer idToSend = idToSendQueue.poll();
                SocketCommunicationOperation operation = operations.get(idToSend);
                JSONObject request = operation.getContent();
                if (operation.waitForResponse())
                    request.put("client_operation_id", idToSend);

                try {
                    CommunicationUtil.sendTo(socketConnection.out, request);
                } catch (IOException e) {
                    operation.getOnErrorEncountered().accept(e.toString());
                    WindowLogger.getInstance().logError(
                            getClass().getName(),
                            "Failed to send request: " + request + " (" + e + ")"
                    );
                    removeOperation(idToSend);
                }
                System.out.println("SENDING: " + request);
            }

            JSONObject incomingData;
            try {
                incomingData = new JSONObject(CommunicationUtil.readUntilEndFrom(socketConnection.in));
                System.out.println("RECEIVED: " + incomingData);
            } catch (SocketTimeoutException ignored) {
                continue;
            } catch (IOException e) {
                WindowLogger.getInstance().logError(
                        getClass().getName(),
                        "Could not read data from socket stream. (" + e + ")"
                );
                continue;
            }
            if (incomingData.has("client_operation_id")) {
                finaliseOperationForResponse(incomingData);
            } else if (incomingData.has("operation")) {
                incomingDataHandler.handle(incomingData);
            } else {
                logger.logInfo(
                        getClass().getName(),
                        "Data was received from socket stream but could not be handled."
                );
            }
        }
    }
}
