package com.example.kuberneteswebsocketintegration.service.tracking;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
// import org.testcontainers.utility.DockerImageName;

import com.example.kuberneteswebsocketintegration.util.endpoint.Endpoints;
import com.example.kuberneteswebsocketintegration.util.endpoint.TestServer;
import com.example.kuberneteswebsocketintegration.util.environment.K3sEnvironment;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

// import lombok.extern.slf4j.Slf4j;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PodTrackingServiceTests extends K3sEnvironment{
    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setUp() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    /**
     * Tests if received payload of pod data
     * equals to the expected payload
     */
    @Test
    public void verifyPayloadIsReceived() {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = null;
        try {
            session = webSocketStompClient
                    .connect(TestServer.getServerEndpoint(port), new StompSessionHandlerAdapter() {
                    })
                    .get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        session.subscribe(Topics.POD, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((String) payload);
            }
        });

        
        session.send(TestServer.getDestinationPath(Endpoints.POD), null);
        await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals("Hello, Mike!", blockingQueue.poll()));
    }
}
