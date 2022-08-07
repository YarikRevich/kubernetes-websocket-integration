package com.example.kuberneteswebsocketintegration.service.kubernetes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class KubernetesServiceTests {
    @Container
    public static K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.21.3-k3s1"))
            .withLogConsumer(new Slf4jLogConsumer(log));

    @Autowired
    KubernetesService kubernetesService;

    private ApiClient apiClient;

    @BeforeEach
    public void setUp(){
        try {
            apiClient = Config.fromConfig(new StringReader(k3s.getKubeConfigYaml()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testGetAllPods(){
        CoreV1Api api = new CoreV1Api();
        api.setApiClient(apiClient);

        V1PodList podList = null;
        try {
            podList = api.listPodForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }

        assertEquals(podList, kubernetesService.getAllPods());
    }
    public void testGetAllServices(){
        CoreV1Api api = new CoreV1Api();
        api.setApiClient(apiClient);

        V1ServiceList serviceList = null;
        try {
            serviceList = api.listServiceForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }

        assertEquals(serviceList, kubernetesService.getAllServices());
    }
    public void testGetAllNodes(){
        CoreV1Api api = new CoreV1Api();
        api.setApiClient(apiClient);

        V1NodeList nodeList = null;
        try {
            nodeList = api.listNode(null, false, null, null, null, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }

        assertEquals(nodeList, kubernetesService.getAllNodes());
    }
}
