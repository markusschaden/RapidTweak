package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;

public class AndroidAppWebSocketServer extends org.java_websocket.server.WebSocketServer {

    private final Logger LOGGER = LoggerFactory.getLogger(AndroidAppWebSocketServer.class);

    private MessageHandler messageHandler;

    public AndroidAppWebSocketServer(int port, MessageHandler messageHandler) {
        super(new InetSocketAddress(port));
        this.messageHandler = messageHandler;
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

        messageHandler.onMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.info("Error:", ex);
    }


    void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }

    interface MessageHandler {
        void onMessage(String message);
    }
}
