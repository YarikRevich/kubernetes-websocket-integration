package com.example.kuberneteswebsocketintegration.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.example.kuberneteswebsocketintegration.util.endpoint.Endpoints;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    ObjectMapper objectMapper = converter.getObjectMapper();
    objectMapper.findAndRegisterModules();
    converters.add(converter);
    return false;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration){
    registration.setMessageSizeLimit(30 * 10000);
    registration.setSendBufferSizeLimit(60 * 10000);
  }
}
