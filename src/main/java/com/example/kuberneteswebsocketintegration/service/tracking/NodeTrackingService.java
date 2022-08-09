package com.example.kuberneteswebsocketintegration.service.tracking;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;
import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.service.tracking.common.ITrackingService;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.models.V1NodeList;
import lombok.extern.slf4j.Slf4j;

/**
 * Fetches node data from Kubernetes cluster
 * in a certain thread
 */
@Slf4j
@Service
public class NodeTrackingService implements ITrackingService {
    @Autowired
    private KubernetesService kubernetesService;

    /**
     * Handles service request to fetch pod data in some
     * period of time
     */
    public void handle(SimpMessagingTemplate template) {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);
        Runnable task = new Runnable() {
            public void run() {
                ResponseEntity<V1NodeList> response = new ResponseEntity<>();
                response.setKind(ResponseEntity.Kind.NodeList);
                response.setContent(kubernetesService.getAllNodes());

                template.convertAndSend(Topics.NODE, response);
                log.info(String.format("Send data to %s topic", Topics.NODE));
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
    }
}
