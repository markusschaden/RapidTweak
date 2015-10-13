package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class ExperimentEntry {

    private long duration;
    private long acceleartionTime;
    private boolean penaltyOccured;

    public ExperimentEntry(long accelerationTime, long duration) {
        this.acceleartionTime = accelerationTime;
        this.duration = duration;
    }
}
