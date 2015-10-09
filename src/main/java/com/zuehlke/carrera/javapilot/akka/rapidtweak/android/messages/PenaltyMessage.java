package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PenaltyMessage extends Message {

    private String sourceId;

    public PenaltyMessage(String barrier) {
        this.sourceId = barrier;
    }
}
