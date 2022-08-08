package com.example.kuberneteswebsocketintegration.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

@org.springframework.context.annotation.Configuration
public class KubernetesConfig {
    /**
     * Configures local Kubernetes cluster config
     */
    // @PostConstruct
    // public void configureKubernetesConfig() {
    //     ApiClient client = null;
    //     try {
    //         client = Config.defaultClient();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     Configuration.setDefaultApiClient(client);
    // }
}
