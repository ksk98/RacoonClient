package com.bots.RacoonClient.Communication;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class SocketConnection {
    public final SSLSocket socket;
    public final PrintWriter out;
    public final DataInputStream in;

    public SocketConnection(SSLSocket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public boolean isClosed() {
        return socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown();
    }
}
