package com.example.kuberneteswebsocketintegration.service.tracking;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kuberneteswebsocketintegration.service.tracking.common.TrackingService;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

import lombok.extern.slf4j.Slf4j;


/**
 * Fetches node data from Kubernetes cluster
 * in a certain thread
 */
@Slf4j
@Service
public class NodeTrackingService implements TrackingService {
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Fetches data about nodes from local kubernetes client
     * @return payload for request handler
     */
    public static Object getPayload() {
        ApiClient client = null;
        try {
            client = Config.defaultClient();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = null;
        try {
            list = api.listPodForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Handles service request to fetch pod data in some
     * period of time
     */
    public void handle() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                template.convertAndSend(Topics.NODE, PodTrackingService.getPayload());
                log.info(String.format("Send data to %s topic", Topics.NODE));
            }
        };
        executor.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
    }
}
