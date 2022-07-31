package com.example.kuberneteswebsocketintegration.util.environment;

import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public class K3sEnvironment {
    private K3sContainer k3s;

    public K3sEnvironment() {
        DockerImageName dockerImage = DockerImageName.parse("rancher/k3s:v1.21.3-k3s1");
        this.k3s = new K3sContainer(dockerImage)
                .withLogConsumer(new Slf4jLogConsumer(log));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.k3s.close();
        }));
        
    }

    /**
     * 
     * @return config for local Kubernetes cluster
     */
    public String getClusterConfig(){
        return this.k3s.getKubeConfigYaml();
    }
}
