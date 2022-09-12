package com.bots.RaccoonClient.Communication;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class SocketConnection {
    public final SSLSocket socket;
    public final BufferedWriter out;
    public final DataInputStream in;

    public SocketConnection(SSLSocket socket) throws IOException {
        this.socket = socket;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new DataInputStream(socket.getInputStream());
    }

    public boolean isClosed() {
        return socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown();
    }
}
