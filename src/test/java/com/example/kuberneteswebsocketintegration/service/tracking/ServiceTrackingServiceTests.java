package com.example.kuberneteswebsocketintegration.service.tracking;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.kuberneteswebsocketintegration.config.KubernetesConfig;
import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @EnableAutoConfiguration(exclude=KubernetesConfig.class)
public class ServiceTrackingServiceTests {
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
}
