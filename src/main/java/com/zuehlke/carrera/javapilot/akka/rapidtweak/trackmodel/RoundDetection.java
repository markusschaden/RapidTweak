package com.zuehlke.carrera.javapilot.akka.rapidtweak.trackmodel;

import com.zuehlke.carrera.javapilot.akka.Configuration;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.CircularArrayList;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.SpeedMeasureTrackElement;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Markus on 12.11.2015.
 */
public class RoundDetection {

    private final Logger LOGGER = LoggerFactory.getLogger(RoundDetection.class);


    public boolean isRound(Race race) {
        if (race == null || race.getFilteredTrack() == null) return false;
        int size = race.getFilteredTrack().size();
        long duration = new Date().getTime() - race.getTime();
        if (size % 2 == 1 || size < 4 || duration < 4000) return false; //min duration = 4sec

        List<TrackElement> round1 = race.getFilteredTrack().subList(0, size / 2);
        List<TrackElement> round2 = race.getFilteredTrack().subList(size / 2, size);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        long time1 = 0, time2 = 0;
        for (int i = 0; i < round1.size(); i++) {
            time1 += round1.get(i).getDurations().get(0).getTime();
            time2 += round2.get(i).getDurations().get(0).getTime();

            sb1.append(round1.get(i).getClass().getSimpleName()).append(",");
            sb2.append(round2.get(i).getClass().getSimpleName()).append(",");

            if (!round1.get(i).getClass().getSimpleName().equals(round2.get(i).getClass().getSimpleName())) {
                sb1.append("   Time:").append(time1);
                sb2.append("   Time:").append(time2);
                //LOGGER.info("Round1: " + sb1.toString());
                //LOGGER.info("Round2: " + sb2.toString());

                return false;
            }
        }

        sb1.append("   Time:").append(time1);
        sb2.append("   Time:").append(time2);
        //LOGGER.info("Round1: " + sb1.toString());
        //LOGGER.info("Round2: " + sb2.toString());


        LOGGER.error("Round matched, diff " + Math.abs(time1 - time2) + "ms");

        if (Math.abs(time1 - time2) < Configuration.DELTA) {
            LOGGER.error("Round matched within delta");
            return true;
        }

        return false;
    }

    public void createRoundTrack(Race race) {
        int size = race.getTrack().size();
        List<TrackElement> trackElements = new CircularArrayList<>();
        for (int i = 0; i < size / 2; i++) {
            trackElements.add(race.getTrack().get(i));
        }
        race.setTrack(trackElements);

        size = race.getSpeedMeasureTrackElements().size();
        LOGGER.info("SpeedMeasureTrackElements before: " + race.getSpeedMeasureTrackElements().size());
        Map<String, SpeedMeasureTrackElement> m = new HashMap<>();
        for (int i = 0; i < size / 2; i++) {
            m.put("" + i, race.getSpeedMeasureTrackElements().get("" + i));
        }
        race.setSpeedMeasureTrackElements(m);
        LOGGER.info("SpeedMeasureTrackElements after: " + race.getSpeedMeasureTrackElements().size());
    }

}
