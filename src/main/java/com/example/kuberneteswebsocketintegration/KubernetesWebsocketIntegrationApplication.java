package com.example.kuberneteswebsocketintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.kubernetes.client.util.Config;

@SpringBootApplication
public class KubernetesWebsocketIntegrationApplication {
	public static void main(String[] args) {
		try {
			Config.defaultClient();
		} catch (Exception e){
			e.printStackTrace();
		}

		SpringApplication.run(KubernetesWebsocketIntegrationApplication.class, args);
	}
}
