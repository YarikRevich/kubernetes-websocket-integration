package com.example.kuberneteswebsocketintegration.service.tracking.common;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface ITrackingService {
    /**
     * Handles request for a certain type of resource
     */
    public void handle(SimpMessagingTemplate template);
}
