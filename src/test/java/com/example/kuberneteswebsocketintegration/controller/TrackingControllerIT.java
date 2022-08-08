package com.example.kuberneteswebsocketintegration.controller;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.util.converter.Converters;
import com.example.kuberneteswebsocketintegration.util.endpoint.TestServer;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TrackingControllerIT {
    @LocalServerPort
    private Integer port;

    @Autowired
    private KubernetesService kubernetesService;

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
    public void testSendPods() throws Exception {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient
                .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        log.info("Connection initialized");

                        session.subscribe(Topics.POD, new StompSessionHandlerAdapter() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                log.info("Payload type received!");
                                return String.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                log.info("Payload received!");
                                blockingQueue.add((String) payload);
                            }

                            @Override
                            public void handleException(StompSession session, @Nullable StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
                                log.info("Exception received!");
                            }
                        });

                        // subscription.addReceiptTask(new Thread(() -> {
                        //     log.info("Receipt task started!");
                        // }));
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        session.send("/app/pod", "gfkjk");


                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).get(1, TimeUnit.SECONDS);
    }

    // @Test
    // void verifyGreetingIsReceived() throws Exception {
    // BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

    // webSocketStompClient
    // .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter()
    // {
    // @Override
    // public void afterConnected(StompSession session, StompHeaders
    // connectedHeaders) {
    // log.info("Connection initialized");

    // Subscription subscription = session.subscribe(Topics.POD, new
    // StompSessionHandlerAdapter() {
    // @Override
    // public Type getPayloadType(StompHeaders headers) {
    // log.info("Payload type received!");
    // return byte[].class;
    // }

    // @Override
    // public void handleFrame(StompHeaders headers, Object payload) {
    // log.info("Payload received!");
    // blockingQueue.add((String) payload);
    // }

    // @Override
    // public void handleException(StompSession session, @Nullable StompCommand
    // command,
    // StompHeaders headers, byte[] payload, Throwable exception) {
    // log.info("Exception received!");
    // }
    // });

    // subscription.addReceiptTask(new Thread(() -> {
    // log.info("Receipt task started!");
    // }));

    // session.send("/app/pod", "gfkjk");
    // }

    // @Override
    // public void handleFrame(StompHeaders headers, Object payload) {
    // log.info("Connection frame");
    // // blockingQueue.add((String) payload);
    // }
    // })
    // .get(1, TimeUnit.SECONDS);

    // TimeUnit.SECONDS.sleep(50);
    // // TimeUnit.SECONDS.sleep(6);
    // // assertNotNull(blockingQueue.poll());
    // System.out.println(blockingQueue.poll());
    // // await()
    // // .atMost(5, TimeUnit.SECONDS)
    // // .untilAsserted(() -> assertNotNull(blockingQueue.poll()));
    // // .untilAsserted(() -> assertEquals(kubernetesService.getAllPods(), ));
    // // blockingQueue.poll()));
    // }
}




