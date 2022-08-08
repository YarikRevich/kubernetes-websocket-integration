package com.example.kuberneteswebsocketintegration.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import com.example.kuberneteswebsocketintegration.config.KubernetesConfig;
import com.example.kuberneteswebsocketintegration.config.SecurityConfig;
import com.example.kuberneteswebsocketintegration.config.WebSocketConfig;
import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;
import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.util.converter.Converters;
import com.example.kuberneteswebsocketintegration.util.formater.ResponseFormater;
import com.example.kuberneteswebsocketintegration.util.server.TestServer;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class TrackingControllerTests {
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

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(600 * 10000);
        container.setDefaultMaxTextMessageBufferSize(600 * 10000);
        WebSocketClient transport = new StandardWebSocketClient(container);

        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(transport))));
        webSocketStompClient.setMessageConverter(Converters.getTestGsonMessageConverter());
    }

    @Test
    void verifyGreetingIsReceived() throws Exception {
        BlockingQueue<ResponseEntity<V1PodList>> blockingQueue = new ArrayBlockingQueue<>(100);

        StompSession session = webSocketStompClient
                .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe(Topics.POD, new StompSessionHandlerAdapter() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ResponseEntity.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((ResponseEntity<V1PodList>) payload);
            }
        });

        session.send("/app/pod", "");

        await()
                .atLeast(10, SECONDS)
                .untilAsserted(() -> {
                    ResponseEntity<V1PodList> expectedResponse = ResponseFormater
                            .formatResponseFromPodList(kubernetesService.getAllPods());
                    ResponseEntity<V1PodList> receivedResponse = blockingQueue.poll();

                    assertEquals(expectedResponse.getKind(),
                            receivedResponse.getKind());
                    assertEquals(expectedResponse.getContent().getItems().size(),
                            receivedResponse.getContent().getItems().size());
                });
    }
}
