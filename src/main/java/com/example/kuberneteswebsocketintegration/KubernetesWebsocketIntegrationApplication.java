package com.example.kuberneteswebsocketintegration;

import java.io.IOException;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KubernetesWebsocketIntegrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(KubernetesWebsocketIntegrationApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(Environment environment) {
		return args -> {
			ApiClient client = null;
			try {
				client = Config.defaultClient();
			} catch (IOException e) {
				log.error("Local Kubernetes cluster is not running");
			}
			Configuration.setDefaultApiClient(client);
		};
	}
}
