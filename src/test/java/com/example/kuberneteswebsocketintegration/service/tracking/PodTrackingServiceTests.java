package com.example.kuberneteswebsocketintegration.service.tracking;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.util.endpoint.TestServer;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PodTrackingServiceTests {
    @LocalServerPort
    private Integer port;

    @Autowired
    private KubernetesService kubernetesService;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setUp() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    // webSocketStompClient.setMessageConverter(new StringMessageConverter());
    // webSocketStompClient.setMessageConverter(new ByteArrayMessageConverter());
    // webSocketStompClient.setMessageConverter(new
    // MappingJackson2MessageConverter());


    @Test
    void verifyGreetingIsReceived() throws Exception {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = webSocketStompClient
                .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe(Topics.POD, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // System.out.println(payload);
                blockingQueue.add((String) payload);
            }
        });

        TimeUnit.SECONDS.sleep(5);

        session.send("/app/pod", "");

        TimeUnit.SECONDS.sleep(15);
        // assertNotNull(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        // await()
                // .atMost(5, TimeUnit.SECONDS)
                // .untilAsserted(() -> assertNotNull(blockingQueue.poll()));
                // .untilAsserted(() -> assertEquals(kubernetesService.getAllPods(), ));
        // blockingQueue.poll()));
    }
}
