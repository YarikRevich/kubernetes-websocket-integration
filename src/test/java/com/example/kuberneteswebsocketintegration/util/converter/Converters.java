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
    public static Gson getGsonInstance(){
        JsonSerializer<OffsetDateTime> serializer = new JsonSerializer<OffsetDateTime>() {
            @Override
            public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject result = new JsonObject();

                result.addProperty("dateTime", src.toString());

                return result;
            }
        };
        return new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, serializer)
                .create();
    }


    public static GsonMessageConverter getTestGsonMessageConverter() {
        GsonMessageConverter converter = new GsonMessageConverter();
        converter.setGson(Converters.getGsonInstance());
        return converter;
    }

    // public static void convertGsonArrayToArray(Object object, Type type){
    //     // Converters.getGsonInstance().fromJson(object, type);
    // }
}
