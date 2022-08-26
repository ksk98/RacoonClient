package com.bots.RacoonClient.Communication;

import com.bots.RacoonClient.Events.IncomingDataEvents.Abstractions.IncomingDataTrafficHandler;
import com.bots.RacoonClient.Events.IncomingDataEvents.IncomingLogHandler;
import com.bots.RacoonClient.Util.CommunicationUtil;
import com.bots.RacoonClient.WindowLogger;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TrafficManager implements Runnable {
    private boolean running = false;
    private PrintWriter out;
    private DataInputStream in;

    private Map<Integer, SocketCommunicationOperation> operations;
    private Queue<Integer> idToSendQueue;
    private int nextId;

    private IncomingDataTrafficHandler incomingDataHandler;

    public TrafficManager(PrintWriter out, DataInputStream in) {
        this.out = out;
        this.in = in;

        this.operations = new HashMap<>();
        this.idToSendQueue = new LinkedList<>();
        this.nextId = 0;

        incomingDataHandler = new IncomingLogHandler();
    }

    public void stop() {
        running = false;
    }

    public void queueOperation(SocketCommunicationOperation operation) {
        operations.put(nextId, operation);
        nextId += 1;
    }

    private void finaliseOperationForResponse(JSONObject response) {
        int id = response.getInt("operation_id");
        SocketCommunicationOperation operation = operations.remove(id);
        operation.onResponse.accept(response);

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
                    JSONObject request = operations.get(idToSend).request.append("operation_id", idToSend);
                    out.write(request.toString());

                    if (out.checkError()) {
                        operations.get(idToSend).onError.accept("PrintWriter failed to send request: " + request);
                        removeOperation(idToSend);
                    }
                }

                JSONObject incomingData = new JSONObject(CommunicationUtil.readUntilEndFrom(in));
                if (incomingData.has("operation_id")) {
                    finaliseOperationForResponse(incomingData);
                } else if (incomingData.has("operation")) {
                    incomingDataHandler.handle(incomingData);
                } else {
                    WindowLogger.getInstance().logInfo("Data was received from socket stream but could not be handled.");
                }

            } catch (IOException e) {
                WindowLogger.getInstance().logError(e.getMessage());
            }
        }
    }
}
