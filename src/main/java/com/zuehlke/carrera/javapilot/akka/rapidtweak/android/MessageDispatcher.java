package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.google.gson.Gson;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MessageDispatcher {

    private final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);
    private List<MessageEndpoint> endpoints = new ArrayList<>();

    public MessageDispatcher() {

    }


    public void addEndpoint(MessageEndpoint endpoint) {
        endpoints.add(endpoint);
    }


    public void onMessage(String message) {

        String parts[] = message.split("|");
        if(parts.length == 2) {
            String className = parts[0];
            String data = parts[1];

            try {
                Message rawMessage = new Gson().fromJson(data, (Type) Class.forName(className));
                dispatchMessge(rawMessage);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            LOGGER.error("Invalid message: " + message);
        }
    }

    private void dispatchMessge(Message rawMessage) {
        if(rawMessage instanceof ManualSpeedMessage) {
            ManualSpeedMessage messageClass = (ManualSpeedMessage)rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onManualSpeedMessage(messageClass));
        } else if(rawMessage instanceof ConfigurationMessage) {
            ConfigurationMessage messageClass = (ConfigurationMessage)rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onConfigurationMessage(messageClass));
        } else if(rawMessage instanceof MonitoringMessage) {
            MonitoringMessage messageClass = (MonitoringMessage)rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onMonitoringMessage(messageClass));
        } else if(rawMessage instanceof RoundTimeMessage) {
            RoundTimeMessage messageClass = (RoundTimeMessage)rawMessage;
            endpoints.forEach((endpoint) -> endpoint.onRoundTimeMessage(messageClass));
        }
    }


}
