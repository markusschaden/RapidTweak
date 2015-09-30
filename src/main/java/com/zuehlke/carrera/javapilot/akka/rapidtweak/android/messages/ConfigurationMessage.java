package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class ConfigurationMessage extends Message {

    Map<String, String> configuration;
}
