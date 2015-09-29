package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.ConfigurationMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.ManualSpeedMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage;

public interface MessageEndpoint {

    void onConfigurationMessage(ConfigurationMessage message);
    void onManualSpeedMessage(ManualSpeedMessage message);
    void onMonitoringMessage(MonitoringMessage message);
    void onRoundTimeMessage(RoundTimeMessage message);

}
