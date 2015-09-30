package com.zuehlke.carrera.javapilot.akka.rapidtweak.android;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Markus on 30.09.2015.
 */
public class MultimapAdapter implements JsonDeserializer<Multimap<String, ?>>, JsonSerializer<Multimap<String, ?>> {
    @Override
    public Multimap<String, ?> deserialize(JsonElement json, Type type,
                                           JsonDeserializationContext context) throws JsonParseException {
        final HashMultimap<String, Object> result = HashMultimap.create();
        final Map<String, Collection<Object>> map = context.deserialize(json, multimapTypeToMapType(type));
        for (final Map.Entry<String, Collection<Object>> e : map.entrySet()) {
            result.putAll(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public JsonElement serialize(Multimap<String, ?> src, Type type, JsonSerializationContext context) {
        final Map<?, ?> map = src.asMap();
        return context.serialize(map);
    }

    private <V> Type multimapTypeToMapType(Type type) {
        final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
        assert typeArguments.length == 2;
        @SuppressWarnings("unchecked")
        final TypeToken<Map<String, Collection<V>>> mapTypeToken = new TypeToken<Map<String, Collection<V>>>() {
        }
                .where(new TypeParameter<V>() {
                }, (TypeToken<V>) TypeToken.of(typeArguments[1]));
        return mapTypeToken.getType();
    }

}
