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
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                template.convertAndSend(Topics.NODE, kubernetesService.getAllNodes());
                log.info(String.format("Send data to %s topic", Topics.NODE));
            }
        };
        executor.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
    }
}
