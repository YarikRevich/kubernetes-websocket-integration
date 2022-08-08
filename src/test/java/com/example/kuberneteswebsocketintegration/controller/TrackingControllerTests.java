package com.example.kuberneteswebsocketintegration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import com.example.kuberneteswebsocketintegration.config.WebSocketConfig;
import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;
import com.example.kuberneteswebsocketintegration.util.converter.Converters;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// @RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ContextConfiguration(classes = {
// WebSocketConfig.class,
// TrackingControllerTests.TestConfig.class
// })
// @AutoConfigureMockMvc
@Testcontainers
class TrackingControllerTests {

    @LocalServerPort
    private Integer port;

    // @Autowired
    // private MockMvc mockMvc;

    // @Autowired
    // private AbstractSubscribableChannel clientInboundChannel;

    // @Autowired
    // private AbstractSubscribableChannel clientOutboundChannel;

    // @Autowired
    // private AbstractSubscribableChannel brokerChannel;

    // private TestInterceptor clientOutboundChannelInterceptor;

    // private TestInterceptor brokerChannelInterceptor;

    // @BeforeEach
    // public void setUp() throws Exception {
    // this.brokerChannelInterceptor = new TestInterceptor();
    // this.clientOutboundChannelInterceptor = new TestInterceptor();

    // this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    // this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
    // }

    // @Test
    // public void testSendPod() throws Exception{
    // StompHeaderAccessor headers =
    // StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
    // headers.setSubscriptionId("0");
    // headers.setDestination("/app/pod");
    // headers.setSessionId("0");
    // headers.setSessionAttributes(new HashMap<>());
    // Message<byte[]> message = MessageBuilder.createMessage(new byte[0],
    // headers.getMessageHeaders());

    // this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/pod");
    // this.clientInboundChannel.send(message);

    // Message<?> reply = this.clientOutboundChannelInterceptor.awaitMessage(10);
    // assertNotNull(reply);

    // StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
    // assertEquals("0", replyHeaders.getSessionId());
    // assertEquals("0", replyHeaders.getSubscriptionId());
    // assertEquals("/app/pod", replyHeaders.getDestination());

    // // String json = new String((byte[]) reply.getPayload(),
    // Charset.forName("UTF-8"));
    // // new JsonPathExpectationsHelper("$[0].company").assertValue(json, "Citrix
    // Systems, Inc.");
    // // new JsonPathExpectationsHelper("$[1].company").assertValue(json, "Dell
    // Inc.");
    // // new JsonPathExpectationsHelper("$[2].company").assertValue(json,
    // "Microsoft");
    // // new JsonPathExpectationsHelper("$[3].company").assertValue(json,
    // "Oracle");
    // }

    private WebSocketStompClient webSocketStompClient;

    @Container
    public static K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.21.3-k3s1"))
            .withLogConsumer(new Slf4jLogConsumer(log));

    @BeforeEach
    public void setUp() {
        ApiClient client = null;
        try {
            client = Config.fromConfig(new StringReader(k3s.getKubeConfigYaml()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration.setDefaultApiClient(client);

        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(Converters.getTestGsonMessageConverter());
    }

    @Test
    void verifyGreetingIsReceived() throws Exception {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(100);

        StompSession session = webSocketStompClient
                .connect(getWsPath(), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/pod", new StompSessionHandlerAdapter() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(headers);
                // blockingQueue.add((String) payload);
            }
        });

        session.send("/app/pod", "gjfkg");

        await()
                .atLeast(10, SECONDS)
                .untilAsserted(() -> assertEquals("Hello, Mike!", blockingQueue.poll()));
    }

    private String getWsPath() {
        return String.format("ws://localhost:%d/ws", port);
    }
}
