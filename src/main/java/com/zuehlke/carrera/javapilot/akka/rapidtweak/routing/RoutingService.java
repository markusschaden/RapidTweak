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
import com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer.TrackOptimizer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.race.RaceStatus;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.service.ServiceManager;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.TrackModeler;
import com.zuehlke.carrera.relayapi.messages.*;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingService implements MessageEndpoint, ClientHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(RoutingService.class);

    RaceStatus raceStatus = RaceStatus.UNDEFINED;
    Race race;
    TrackModeler trackModeler;
    TrackOptimizer trackOptimizer;
    RaceDatabase raceDatabase;
    TrackCoordinateCalculator trackModelerCoordinateCalculator;

    public RoutingService(ActorRef pilot, UntypedActor actor) {
        race = new Race();
        trackModelerCoordinateCalculator = new TrackCoordinateCalculator();
        trackModeler = new TrackModeler(race, trackModelerCoordinateCalculator);
        trackModeler.setPower(100);
        trackOptimizer = new TrackOptimizer(race);
        raceDatabase = new RaceDatabase();

        ServiceManager.getInstance().getPowerService().init(pilot, actor);
        ServiceManager.getInstance().getPowerService().addPowerNotifier(trackModeler);
        ServiceManager.getInstance().getPowerService().addPowerNotifier(trackOptimizer);

        ServiceManager.getInstance().getMessageDispatcher().addMessageEndpoint(this);
        ServiceManager.getInstance().getMessageDispatcher().addNewClientHandler(this);
    }

    public void onPenaltyMessage(PenaltyMessage message) {

        switch(raceStatus) {
            case RACE:
                trackOptimizer.onPenalyMessage(message);

                break;
        }
    }


    public void onVelocityMessage(VelocityMessage message) {

        switch(raceStatus) {

            case LEARN:
                trackModeler.onVelocityMessage(message);

                break;

            case RACE:
                trackOptimizer.onVelocityMessage(message);

                break;
        }
    }

    public void onSensorEvent(SensorEvent event) {
        switch (raceStatus) {

            case LEARN:
                trackModeler.onSensorEvent(event);

                break;

            case RACE:
                trackOptimizer.onSensorEvent(event);

                break;
        }
    }

    public void onRoundTimeMessage(RoundTimeMessage message) {

        switch(raceStatus) {

            case UNDEFINED:

                trackModeler.onRoundTimeMessage(message);
                raceStatus = RaceStatus.LEARN;
                LOGGER.info("onRoundTimeMessage | RaceStatus: " + raceStatus);

                break;

            case LEARN:
                trackModeler.onRoundTimeMessage(message);
                raceStatus = RaceStatus.RACE;
                LOGGER.info("onRoundTimeMessage | RaceStatus: " + raceStatus);
                trackOptimizer.optimize(message);

                break;

            case RACE:
                trackOptimizer.onRoundTimeMessage(message);

                break;
        }
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
