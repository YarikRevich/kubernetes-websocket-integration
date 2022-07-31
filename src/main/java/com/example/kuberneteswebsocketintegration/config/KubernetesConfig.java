package com.example.kuberneteswebsocketintegration.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

@org.springframework.context.annotation.Configuration
public class KubernetesConfig {
    /**
     * Configures local Kubernetes cluster config 
     */
    @Bean
    public void configureKubernetesConfig(){
        ApiClient client = null;
        try {
            client = Config.defaultClient();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Configuration.setDefaultApiClient(client);
    }
}
