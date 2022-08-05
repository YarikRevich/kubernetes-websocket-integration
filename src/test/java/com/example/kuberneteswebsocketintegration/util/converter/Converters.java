package com.example.kuberneteswebsocketintegration.util.converter;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import org.springframework.messaging.converter.GsonMessageConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Converters {
    public static GsonMessageConverter getTestGsonMessageConverter() {
        JsonSerializer<OffsetDateTime> serializer = new JsonSerializer<OffsetDateTime>() {
            @Override
            public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject result = new JsonObject();

                result.addProperty("dateTime", src.toString());

                return result;
            }
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, serializer)
                .create();
        GsonMessageConverter converter = new GsonMessageConverter();
        converter.setGson(gson);
        return converter;
    }
}
