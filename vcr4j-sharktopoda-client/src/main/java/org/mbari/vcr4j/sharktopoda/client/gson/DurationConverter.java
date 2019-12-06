package org.mbari.vcr4j.sharktopoda.client.gson;


import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2016-07-11T15:57:00
 */
public class DurationConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Duration.ofMillis(json.getAsLong());
    }

    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toMillis());
    }
}
