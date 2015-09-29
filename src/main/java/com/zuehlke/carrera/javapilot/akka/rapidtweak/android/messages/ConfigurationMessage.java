package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;

import java.util.Map;

@Data
public class ConfigurationMessage extends Message {

    Map<String, String> configuration;
}
