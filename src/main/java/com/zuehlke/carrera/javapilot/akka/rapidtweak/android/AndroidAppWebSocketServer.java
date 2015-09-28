package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Markus on 28.09.2015.
 */
public class AndroidAppWebSocketServer extends org.java_websocket.server.WebSocketServer {

    private final Logger LOGGER = LoggerFactory.getLogger(AndroidAppWebSocketServer.class);
    private List<ServerEventsListener> serverEventsList = new ArrayList<>();

    public AndroidAppWebSocketServer(int port) {
        super(new InetSocketAddress(port));

    }

    public void addServerEventListener(ServerEventsListener serverEvents) {
        serverEventsList.add(serverEvents);
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

        if (message.startsWith("speed=")) {
            String speedString = message.replace("speed=", "");
            try {
                int speed = Integer.parseInt(speedString);
                for(ServerEventsListener listener : serverEventsList) {
                    listener.onSpeedChange(speed);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


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
