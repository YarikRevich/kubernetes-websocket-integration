package com.example.kuberneteswebsocketintegration.controller;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;

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

    // @Container
    // public static K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.21.3-k3s1"))
    //         .withLogConsumer(new Slf4jLogConsumer(log));

    // @BeforeEach
    // public void setUp() {
    //     ApiClient client = null;
    //     try {
    //         client = Config.fromConfig(new StringReader(k3s.getKubeConfigYaml()));
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     Configuration.setDefaultApiClient(client);

    //     this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
    //             List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    //     webSocketStompClient.setMessageConverter(Converters.getTestGsonMessageConverter());
    // }

    // @Test
    // void verifyGreetingIsReceived() throws Exception {
    //     BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

    //     webSocketStompClient
    //             .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
    //                 @Override
    //                 public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    //                     log.info("Connection initialized");

    //                     Subscription subscription = session.subscribe(Topics.POD, new StompSessionHandlerAdapter() {
    //                         @Override
    //                         public Type getPayloadType(StompHeaders headers) {
    //                             log.info("Payload type received!");
    //                             return byte[].class;
    //                         }

    //                         @Override
    //                         public void handleFrame(StompHeaders headers, Object payload) {
    //                             log.info("Payload received!");
    //                             blockingQueue.add((String) payload);
    //                         }

    //                         @Override
    //                         public void handleException(StompSession session, @Nullable StompCommand command,
    //                                 StompHeaders headers, byte[] payload, Throwable exception) {
    //                             log.info("Exception received!");
    //                         }
    //                     });

    //                     subscription.addReceiptTask(new Thread(() -> {
    //                         log.info("Receipt task started!");
    //                     }));

    //                     session.send("/app/pod", "gfkjk");
    //                 }

    //                 @Override
    //                 public void handleFrame(StompHeaders headers, Object payload) {
    //                     log.info("Connection frame");
    //                     // blockingQueue.add((String) payload);
    //                 }
    //             })
    //             .get(1, TimeUnit.SECONDS);

            

    //     TimeUnit.SECONDS.sleep(50);
    //     // TimeUnit.SECONDS.sleep(6);
    //     // assertNotNull(blockingQueue.poll());
    //     System.out.println(blockingQueue.poll());
    //     // await()
    //     // .atMost(5, TimeUnit.SECONDS)
    //     // .untilAsserted(() -> assertNotNull(blockingQueue.poll()));
    //     // .untilAsserted(() -> assertEquals(kubernetesService.getAllPods(), ));
    //     // blockingQueue.poll()));
    // }
}

