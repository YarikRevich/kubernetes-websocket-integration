package com.example.kuberneteswebsocketintegration.service.tracking;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.service.tracking.common.ITrackingService;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ServiceList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceTrackingService implements ITrackingService{
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private KubernetesService kubernetesService;

    /**
     * Fetches data about services from local kubernetes client
     * @return payload for request handler
     */
    public static String getPayload() {
        CoreV1Api api = new CoreV1Api();
        V1ServiceList list = null;
        try {
            list = api.listServiceForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return list.toString();
    }

    /**
     * Handles service request to fetch service data in some
     * period of time
     */
    public void handle() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                template.convertAndSend(Topics.SERVICE, kubernetesService.getAllServices());
                log.info(String.format("Send data to %s topic", Topics.SERVICE));
            }
        };
        executor.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
    }
}
