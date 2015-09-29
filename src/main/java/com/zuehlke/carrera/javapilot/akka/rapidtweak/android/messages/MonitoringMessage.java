package com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import lombok.Data;

@Data
public class MonitoringMessage extends Message {

    TrackElement trackElement;
}
