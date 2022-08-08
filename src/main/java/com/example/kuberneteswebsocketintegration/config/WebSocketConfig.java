package com.example.kuberneteswebsocketintegration.config;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

import com.example.kuberneteswebsocketintegration.util.endpoint.Endpoints;
// import com.fasterxml.jackson.annotation.JsonInclude.Include;
// import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Configuration for used websocket protocol
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes(Endpoints.APPLICATION_DESTINATION);
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> converters) {
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
    converters.add(converter);
    return false;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration.setSendBufferSizeLimit(600 * 10000).setMessageSizeLimit(600 * 10000);
  }
}
