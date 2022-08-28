package com.bots.RacoonClient.Communication;

import com.bots.RacoonShared.Events.IncomingDataEvents.IncomingDataTrafficHandler;
import com.bots.RacoonShared.Logging.Loggers.Logger;
import com.bots.RacoonShared.SocketCommunication.CommunicationUtil;
import com.bots.RacoonShared.SocketCommunication.SocketCommunicationOperation;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/***
 * Class that is used to manage and execute inbound and outbound traffic.
 */
public class TrafficManager extends Thread {
    private boolean running = false;
    private final PrintWriter out;
    private final DataInputStream in;
    private final Logger logger;

    private final Map<Integer, SocketCommunicationOperation> operations;
    private final Queue<Integer> idToSendQueue;
    private int nextId;

    private final IncomingDataTrafficHandler incomingDataHandler;

    /***
     * @param out used in sending outbound traffic
     * @param in used in receiving incoming traffic
     * @param logger used to log necessary information
     * @param handlerChain chain of responsibility that utilises IncomingDataTrafficHandler interface, used in
     *                     handling incoming traffic that is not part of any queued operations
     */
    public TrafficManager(PrintWriter out, DataInputStream in, Logger logger, IncomingDataTrafficHandler handlerChain) {
        this.out = out;
        this.in = in;
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
        nextId += 1;
    }

    private void finaliseOperationForResponse(JSONObject response) {
        int id = response.getInt("operation_id");
        SocketCommunicationOperation operation = operations.remove(id);
        operation.getOnResponse().accept(response);

        if (operations.isEmpty())
            nextId = 0;
    }

    private void removeOperation(int id) {
        operations.remove(id);

        if (operations.isEmpty())
            nextId = 0;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                if (!idToSendQueue.isEmpty()) {
                    Integer idToSend = idToSendQueue.poll();
                    JSONObject request = operations.get(idToSend).getRequest().append("operation_id", idToSend);
                    CommunicationUtil.sendTo(out, request);

                    if (out.checkError()) {
                        operations.get(idToSend).getOnError().accept("PrintWriter failed to send request: " + request);
                        removeOperation(idToSend);
                    }
                }

                JSONObject incomingData = new JSONObject(CommunicationUtil.readUntilEndFrom(in));
                if (incomingData.has("operation_id")) {
                    finaliseOperationForResponse(incomingData);
                } else if (incomingData.has("operation")) {
                    incomingDataHandler.handle(incomingData);
                } else {
                    logger.logInfo("Data was received from socket stream but could not be handled.");
                }

            } catch (IOException e) {
                logger.logError(e.getMessage());
            }
        }
    }
}
