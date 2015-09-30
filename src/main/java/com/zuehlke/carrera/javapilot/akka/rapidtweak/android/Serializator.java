package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Element;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.TrackElement;

/**
 * Created by Markus on 30.09.2015.
 */
public class Serializator<T> {

    private Gson gson;

    public Serializator() {
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                        //.setPrettyPrinting()
                .registerTypeAdapter(ArrayListMultimap.class, new MultimapAdapter())
                .registerTypeAdapter(HashMultimap.class, new MultimapAdapter())
                .registerTypeAdapter(Multimap.class, new MultimapAdapter())
                .registerTypeAdapter(TrackElement.class, new TrackElementAdapter())
                .registerTypeAdapter(Element.class, new ElementAdapter())
                .create();
    }

    public String serialize(T o) {
        return gson.toJson(o);
    }

    public T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
