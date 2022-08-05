package com.example.kuberneteswebsocketintegration.config;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import com.example.kuberneteswebsocketintegration.util.environment.K3sEnvironment;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;


// @Import({K3sEnvironment.class})
public class KubernetesTestConfig {
    // @Autowired
    // K3sEnvironment k3s;

    // /**
    //  * Configures local Kubernetes cluster config for test
    //  */
    // @PostConstruct
    // public void configureKubernetesConfig() {
    //     k3s.start();
        
    // }
}
