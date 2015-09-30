package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage;
import org.junit.Test;

/**
 * Created by Markus on 30.09.2015.
 */

public class SerializatorTest {

    @Test
    public void testDeserialize() throws ClassNotFoundException {

        Serializator<com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage> serializator = new Serializator<>();
        String input = "{\"roundTime\":8193,\"race\":{\"track\":[{\"CLASSNAME\":\"com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement\",\"INSTANCE\":{\"durations\":{\"0\":[273],\"100\":[276],\"110\":[51]},\"speedMeasureTrackElements\":[],\"speeds\":[110],\"positions\":{\"100\":[0,261]}}},{\"CLASSNAME\":\"com.zuehlke.carrera.javapilot.akka.rapidtweak.track.RightCurveTrackElement\",\"INSTANCE\":{\"durations\":{\"0\":[1651],\"100\":[1751,1529]},\"speedMeasureTrackElements\":[],\"speeds\":[],\"positions\":{\"0\":[273],\"100\":[325],\"110\":[312]}}},{\"CLASSNAME\":\"com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement\",\"INSTANCE\":{\"durations\":{\"100\":[2850,2606,2568]},\"speedMeasureTrackElements\":[],\"speeds\":[110,120],\"positions\":{\"0\":[1924],\"100\":[2076,1841]}}},{\"CLASSNAME\":\"com.zuehlke.carrera.javapilot.akka.rapidtweak.track.RightCurveTrackElement\",\"INSTANCE\":{\"durations\":{\"100\":[1799,1658,1570]},\"speedMeasureTrackElements\":[],\"speeds\":[],\"positions\":{\"100\":[4926,4530,4409]}}},{\"CLASSNAME\":\"com.zuehlke.carrera.javapilot.akka.rapidtweak.track.StraightTrackElement\",\"INSTANCE\":{\"durations\":{\"100\":[2521,2684]},\"speedMeasureTrackElements\":[],\"speeds\":[110,120],\"positions\":{\"100\":[6725,6188,5979]}}}],\"speedMeasureTrackElements\":[{\"speeds\":{\"100\":[177.33940648287535]},\"positions\":{\"100\":[-1]}},{\"speeds\":{\"100\":[103.26901470683515]},\"positions\":{\"100\":[1845]}},{\"speeds\":{\"100\":[207.36894339695573]},\"positions\":{\"100\":[4697]}},{\"speeds\":{\"100\":[101.76939385011792]},\"positions\":{\"100\":[6505]}}]}}";

        Class clazz = Class.forName("com.zuehlke.carrera.javapilot.akka.rapidtweak.android.messages.RoundTimeMessage");
        RoundTimeMessage m = serializator.deserialize(input, clazz);

        System.out.println(m.getClass());
    }

    @Test
    public void testSplit() {
        String parts[] = "test|test".split("\\|");
        if (parts.length == 2) {
            String className = parts[0];
            String data = parts[1];
        }
        System.out.println("" + parts.length);
    }

}
