package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.*;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class MessageDispatcher implements AndroidAppWebSocketServer.MessageHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);
    private List<MessageEndpoint> endpoints = new ArrayList<>();
    private List<ClientHandler> clientHandlers = new ArrayList<>();
    private AndroidAppWebSocketServer androidAppWebSocketServer;
    private Serializator<Message> serializator = new Serializator<>();
    public MessageDispatcher() {

    }


    public void addMessageEndpoint(MessageEndpoint endpoint) {
        endpoints.add(endpoint);
    }


    public void onMessage(String message) {

        String parts[] = message.split("\\|");
        if (parts.length == 2) {
            String className = parts[0];
            String data = parts[1];

            try {
                Class clazz = Class.forName(className);
                Message rawMessage = serializator.deserialize(data, clazz);
                dispatchMessge(rawMessage);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }

        } else {
            LOGGER.error("Invalid message: " + message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.onOpen(webSocket);
        }
    }

    private void dispatchMessge(Message rawMessage) {
        if (rawMessage instanceof ManualSpeedMessage) {
            ManualSpeedMessage messageClass = (ManualSpeedMessage) rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onManualSpeedMessage(messageClass));
        } else if (rawMessage instanceof ConfigurationMessage) {
            ConfigurationMessage messageClass = (ConfigurationMessage) rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onConfigurationMessage(messageClass));
        } else if (rawMessage instanceof MonitoringMessage) {
            MonitoringMessage messageClass = (MonitoringMessage) rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onMonitoringMessage(messageClass));
        } else if (rawMessage instanceof RoundTimeMessage) {
            RoundTimeMessage messageClass = (RoundTimeMessage) rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onRoundTimeMessage(messageClass));
        }
    }

    public void sendMessage(Message message) {
        if (androidAppWebSocketServer != null) {
            //LOGGER.info("Send Message: " + message.toString());
            Thread sendMessageThread = new Thread(() -> {
                String json = serializator.serialize(message);
                String className = message.getClass().getCanonicalName();
                androidAppWebSocketServer.sendToAll(className + "|" + json);
            });
            sendMessageThread.start();

            /*String json = serializator.serialize(message);
            String className = message.getClass().getCanonicalName();
            androidAppWebSocketServer.sendToAll(className + "|" + json);*/
        } else {
            LOGGER.error("Cant send message, WebSocketServer is not running");
        }
    }


    public void startServer(int port) {
        if (androidAppWebSocketServer == null) {
            androidAppWebSocketServer = new AndroidAppWebSocketServer(port, this);
            androidAppWebSocketServer.start();
            LOGGER.info("WebSocketServer listening on port " + port);
        }
    }

    public void addNewClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    public void sendSingleMessage(WebSocket webSocket, RaceDrawerMessage raceDrawerMessage) {
        if (androidAppWebSocketServer != null) {
            LOGGER.info("Send Message: " + raceDrawerMessage.toString());
            Thread sendMessageThread = new Thread(() -> {
                LOGGER.debug("message sent");
                String json = serializator.serialize(raceDrawerMessage);
                String className = raceDrawerMessage.getClass().getCanonicalName();
                androidAppWebSocketServer.send(webSocket, className + "|" + json);
            });
            sendMessageThread.start();

            /*String json = serializator.serialize(message);
            String className = message.getClass().getCanonicalName();
            androidAppWebSocketServer.sendToAll(className + "|" + json);*/
        } else {
            LOGGER.error("Cant send message, WebSocketServer is not running");
        }
    }
}
