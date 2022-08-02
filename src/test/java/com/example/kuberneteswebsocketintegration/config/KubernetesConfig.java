package com.example.kuberneteswebsocketintegration.config;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.PostConstruct;

import org.springframework.boot.test.context.TestConfiguration;

import com.example.kuberneteswebsocketintegration.util.environment.K3sEnvironment;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

@TestConfiguration
public class KubernetesConfig extends K3sEnvironment{
    /**
     * Configures local Kubernetes cluster config for test
     */
    @PostConstruct
    public void configureKubernetesConfig() {
        ApiClient client = null;
        try {
            client = Config.fromConfig(new StringReader(this.getClusterConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration.setDefaultApiClient(client);
    }
}
