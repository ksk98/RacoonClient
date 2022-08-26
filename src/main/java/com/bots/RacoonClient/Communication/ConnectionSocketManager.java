package com.bots.RacoonClient.Communication;

import com.bots.RacoonClient.Util.CommunicationUtil;
import com.bots.RacoonClient.WindowManager;
import org.json.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.*;

public class ConnectionSocketManager {
    private static ConnectionSocketManager instance = null;

    private SSLSocket socket = null;
    private boolean connected = false, loggedIn = false;

    private PrintWriter out;
    private DataInputStream in;
    private TrafficManager trafficManager;

    private ConnectionSocketManager() {

    }

    public static ConnectionSocketManager getInstance() {
        if (instance == null)
            instance = new ConnectionSocketManager();

        return instance;
    }

    public void connectTo(String host, int port) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();

        socket = (SSLSocket)factory.createSocket(host, port);
        socket.startHandshake();

        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        in = new DataInputStream(in);
        trafficManager = new TrafficManager(out, in);

        connected = true;
    }

    public void disconnect() throws IOException {
        CommunicationUtil.sendTo(out, new JSONObject().append("operation", "disconnect"));
        in.close();
        out.close();
        socket.close();
        connected = false;
        loggedIn = false;
    }

    public void login(String username, String password) {
        if (!connected)
            return;

        JSONObject loginJSON = new JSONObject()
                .append("operation", "login")
                .append("username", username)
                .append("password", password);

        trafficManager.queueOperation(new SocketCommunicationOperation(
                loginJSON,
                response -> {
                    try {
                        CommunicationUtil.sendTo(out, loginJSON);
                        response = new JSONObject(CommunicationUtil.readUntilEndFrom(in));
                    } catch (IOException e) {
                        displayErrorDialog(e.getMessage());
                    }

                    int responseCode = response.getInt("code");
                    if (responseCode == 200 || responseCode == 204) {
                        WindowManager.getInstance().changeViewTo(WindowManager.View.MAIN);
                    } else {
                        try {
                            displayErrorDialog(response.getString("message"), "Login failed");
                        } catch (JSONException e) {
                            displayErrorDialog("Could not login.", "Login failed");
                        }
                    }
                },
                this::displayErrorDialog)
        );
    }

    private void displayErrorDialog(String message) {
        displayErrorDialog(message, "Error");
    }

    private void displayErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(WindowManager.getInstance().getCurrentView(),
                message, title, JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
