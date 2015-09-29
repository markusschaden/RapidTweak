package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.ManualSpeedMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AndroidAppWebSocketServer extends org.java_websocket.server.WebSocketServer {

    private final Logger LOGGER = LoggerFactory.getLogger(AndroidAppWebSocketServer.class);

    private List<Message> messages = new ArrayList<>();
    private MessageDispatcher messageDispatcher = new MessageDispatcher();

    public AndroidAppWebSocketServer(int port) {
        super(new InetSocketAddress(port));

        messages.add(new ManualSpeedMessage());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("WebSocket new connection:" + conn.toString());

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info("WebSocket closed connection:" + conn.toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOGGER.info("WebSocket recveived:" + message);

        messageDispatcher.onMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.info("Error:", ex);
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }
}
