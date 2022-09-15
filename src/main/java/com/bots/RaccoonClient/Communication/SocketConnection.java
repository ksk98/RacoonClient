package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Exceptions.SocketConnectionCreationException;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class SocketConnection {
    public final SSLSocket socket;
    public final BufferedWriter out;
    public final DataInputStream in;

    public SocketConnection(SSLSocket socket) throws SocketConnectionCreationException {
        this.socket = socket;
        try {
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new SocketConnectionCreationException("Could not setup IO utensils in new Socket Connection: " + e);
        }

    }

    public boolean isClosed() {
        return socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown();
    }
}
