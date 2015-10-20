package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import org.java_websocket.WebSocket;

/**
 * Created by Markus on 20.10.2015.
 */
public interface ClientHandler {

    void onOpen(WebSocket webSocket);
}
