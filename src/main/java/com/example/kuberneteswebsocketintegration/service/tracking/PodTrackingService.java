package com.example.kuberneteswebsocketintegration.service.tracking;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;
import com.example.kuberneteswebsocketintegration.service.kubernetes.KubernetesService;
import com.example.kuberneteswebsocketintegration.service.tracking.common.ITrackingService;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

import io.kubernetes.client.openapi.models.V1PodList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PodTrackingService implements ITrackingService {
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
                try {
                    ResponseEntity<V1PodList> response = new ResponseEntity<>();
                    response.setKind(ResponseEntity.Kind.PodList);
                    response.setContent(kubernetesService.getAllPods());

                    template.convertAndSend(Topics.POD, response);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                log.info(String.format("Send data to %s topic", Topics.POD));
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
    }
}
