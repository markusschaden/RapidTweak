package com.zuehlke.carrera.javapilot.akka.rapidtweak.routing;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.AndroidAppWebSocketServer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.optimizer.TrackOptimizer;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerService;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.race.RaceStatus;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel.TrackModeler;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Markus on 25.09.2015.
 */
public class RoutingService {

    private final Logger LOGGER = LoggerFactory.getLogger(RoutingService.class);

    RaceStatus raceStatus = RaceStatus.UNDEFINED;
    Race race;
    TrackModeler trackModeler;
    TrackOptimizer trackOptimizer;

    public RoutingService(ActorRef pilot, UntypedActor actor) {
        race = new Race();
        trackModeler = new TrackModeler(race);
        trackModeler.setPower(100);
        trackOptimizer = new TrackOptimizer(race);
        PowerService.getInstance().init(pilot, actor);
        PowerService.getInstance().addPowerNotifier(trackModeler);
        PowerService.getInstance().addPowerNotifier(trackOptimizer);
    }

    private void createWebSocketServer() {
        int port = 10500;
        AndroidAppWebSocketServer androidAppWebSocketServer = new AndroidAppWebSocketServer(port);
        androidAppWebSocketServer.start();
        LOGGER.info("WebSocketServer initialized on port " + port);

    }



    public void onPenalyMessage(PenaltyMessage message) {

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


        switch(raceStatus) {

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



}
