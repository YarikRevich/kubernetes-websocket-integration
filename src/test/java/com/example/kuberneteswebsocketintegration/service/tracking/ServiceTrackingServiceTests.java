package com.example.kuberneteswebsocketintegration.service.tracking;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import com.example.kuberneteswebsocketintegration.config.KubernetesConfig;
import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ServiceTrackingServiceTests {
    @Mock
    private SimpMessagingTemplate template;

    @Autowired
    ServiceTrackingService serviceTrackingService;

    @Autowired
    KubernetesService kubernetesService;

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
    }

    @Test
    public void testHandle() {
        serviceTrackingService.handle(template);
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(template, Mockito.atLeastOnce()).convertAndSend(Mockito.eq(Topics.SERVICE),
                        Mockito.any(V1ServiceList.class)));
    }
}
