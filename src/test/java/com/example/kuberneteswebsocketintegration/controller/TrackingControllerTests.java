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
import java.util.concurrent.TimeUnit;

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
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
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
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackingControllerTests {
    @LocalServerPort
    private Integer port;

    @Container
    public static K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.21.3-k3s1"))
            .withLogConsumer(new Slf4jLogConsumer(log));

    @Autowired
    private KubernetesService kubernetesService;

    private WebSocketStompClient webSocketStompClient;

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

    // @Test
    // void testSendPods() throws Exception {
    //     BlockingQueue<ResponseEntity<V1PodList>> blockingQueue = new ArrayBlockingQueue<>(100);

    //     webSocketStompClient
    //             .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
    //                 @Override
    //                 public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    //                     log.info("Connection established!");

    //                     session.subscribe(Topics.POD, new StompSessionHandlerAdapter() {
    //                         @Override
    //                         public Type getPayloadType(StompHeaders headers) {
    //                             return ResponseEntity.class;
    //                         }

    //                         @Override
    //                         public void handleFrame(StompHeaders headers, Object payload) {
    //                             System.out.println("RECEIVED!");
    //                             blockingQueue.add((ResponseEntity<V1PodList>) payload);
    //                         }
    //                     });

    //                     session.send("/app/pod", "");

    //                     await()
    //                             .until(() -> {
    //                                 return blockingQueue.size() > 0;
    //                             });

    //                     // .untilAsserted(() -> {
    //                     ResponseEntity<V1PodList> expectedResponse = ResponseFormater
    //                             .formatResponseFromPodList(kubernetesService.getAllPods());
    //                     ResponseEntity<V1PodList> receivedResponse = blockingQueue.poll();

    //                     System.out.println(expectedResponse.getKind());
    //                     System.out.println(receivedResponse.getKind());

    //                     // assertEquals(expectedResponse.getKind(),
    //                     // receivedResponse.getKind());
    //                     // assertEquals(expectedResponse.getContent().getItems().size(),
    //                     // receivedResponse.getContent().getItems().size());
    //                     // })
    //                 }
    //             })
    //             .get(1, SECONDS);
    // }


    @Test
    void testSendNodes() throws Exception {
        BlockingQueue<ResponseEntity<V1NodeList>> blockingQueue = new ArrayBlockingQueue<>(100);

        webSocketStompClient
                .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        log.info("Connection established!");

                        session.subscribe(Topics.NODE, new StompSessionHandlerAdapter() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return ResponseEntity.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                System.out.println("RECEIVED!");
                                blockingQueue.add((ResponseEntity<V1NodeList>) payload);
                            }
                        });

                        session.send("/app/node", "");

                        await()
                                .until(() -> {
                                    return blockingQueue.size() > 0;
                                });

                        // .untilAsserted(() -> {
                        ResponseEntity<V1NodeList> expectedResponse = ResponseFormater
                                .formatResponseFromNodeList(kubernetesService.getAllNodes());
                        ResponseEntity<V1NodeList> receivedResponse = blockingQueue.poll();

                        System.out.println(expectedResponse.getKind());
                        System.out.println(receivedResponse.getKind());

                        // assertEquals(expectedResponse.getKind(),
                        // receivedResponse.getKind());
                        // assertEquals(expectedResponse.getContent().getItems().size(),
                        // receivedResponse.getContent().getItems().size());
                        // })
                    }
                })
                .get(1, SECONDS);
    }
}
