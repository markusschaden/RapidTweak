package com.zuehlke.carrera.javapilot.akka.rapidtweak.routing;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.ClientHandler;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.MessageEndpoint;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.ConfigurationMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.ManualSpeedMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.MonitoringMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RaceDrawerMessage;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.coordinates.TrackCoordinateCalculator;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.dal.RaceDatabase;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.emergency.EmergencyWatchdog;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.race.RaceStatus;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.state.Context;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.state.StateHandler;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.relayapi.messages.*;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingService implements MessageEndpoint, ClientHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(RoutingService.class);

    RaceStatus raceStatus = RaceStatus.UNDEFINED;
    Race race;
    StateHandler stateHandler;
    RaceDatabase raceDatabase;
    TrackCoordinateCalculator trackModelerCoordinateCalculator;
    EmergencyWatchdog emergencyWatchdog;

    public RoutingService(ActorRef pilot, UntypedActor actor) {
        emergencyWatchdog = new EmergencyWatchdog();

        ServiceManager.getInstance().getPowerService().init(pilot, actor);
        init();
    }


    private void init() {
        Context context = new Context();
        race = new Race();
        context.setRace(race);

        trackModelerCoordinateCalculator = new TrackCoordinateCalculator();
        stateHandler = new StateHandler(context);
        //trackModeler.setPower(100);
        //trackOptimizer = new TrackOptimizer(race);
        raceDatabase = new RaceDatabase();

        ServiceManager.getInstance().getPowerService().reset();
        //ServiceManager.getInstance().getPowerService().addPowerNotifier(trackModeler);
        //ServiceManager.getInstance().getPowerService().addPowerNotifier(trackOptimizer);

        ServiceManager.getInstance().getMessageDispatcher().addMessageEndpoint(this);
        ServiceManager.getInstance().getMessageDispatcher().addNewClientHandler(this);
    }


    public void onPenaltyMessage(PenaltyMessage message) {

        stateHandler.onPenaltyMessage(message);
    }


    public void onVelocityMessage(VelocityMessage message) {

        stateHandler.onVelocityMessage(message);
    }

    public void onSensorEvent(SensorEvent event) {
        if (emergencyWatchdog != null) {
            emergencyWatchdog.onSensorEvent(event);
        }

        stateHandler.onSensorEvent(event);
    }

    public void onRoundTimeMessage(RoundTimeMessage message) {

        stateHandler.onRoundTimeMessage(message);
    }


    @Override
    public void onConfigurationMessage(ConfigurationMessage message) {
        LOGGER.info("Not implemented");
    }

    @Override
    public void onManualSpeedMessage(ManualSpeedMessage message) {
        LOGGER.info("Not implemented");
    }

    @Override
    public void onMonitoringMessage(MonitoringMessage message) {
        LOGGER.warn("This Message type is not supported");
    }

    @Override
    public void onRoundTimeMessage(com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage message) {
        LOGGER.warn("This Message type is not supported");
    }

    public void onRaceStop(RaceStopMessage message) {
        synchronized (emergencyWatchdog) {
            emergencyWatchdog.cancel();
            emergencyWatchdog = null;
        }
        stateHandler.onRaceStopMessage(message);
        raceDatabase.insertRace(race);
    }

    public void onRaceStart(RaceStartMessage message) {
        race.setTrackId(message.getTrackId());
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        if (raceStatus == RaceStatus.RACE) {
            RaceDrawerMessage raceDrawerMessage = new RaceDrawerMessage(trackModelerCoordinateCalculator.getTrack());
            ServiceManager.getInstance().getMessageDispatcher().sendSingleMessage(webSocket, raceDrawerMessage);
        }
    }

}
