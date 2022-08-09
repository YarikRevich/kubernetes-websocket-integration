package com.example.kuberneteswebsocketintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import com.example.kuberneteswebsocketintegration.service.tracking.NodeTrackingService;
import com.example.kuberneteswebsocketintegration.service.tracking.PodTrackingService;
import com.example.kuberneteswebsocketintegration.service.tracking.ServiceTrackingService;
import com.example.kuberneteswebsocketintegration.util.endpoint.Endpoints;
import com.example.kuberneteswebsocketintegration.util.topic.Topics;

/**
 * Tracking controller, which fetches
 * data from local Kubernetes cluster
 */
@Controller
@EnableScheduling
public class TrackingController {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NodeTrackingService nodeTrackerService;

    @Autowired
    private ServiceTrackingService serviceTrackerService;

    @Autowired
    private PodTrackingService podTrackerService;
   
    @MessageMapping(Endpoints.POD)
    @SendTo(Topics.POD)
    public void sendPods() {
        podTrackerService.handle(template);
    }

    @MessageMapping(Endpoints.SERVICE)
    @SendTo(Topics.SERVICE)
    public void sendServices() {
        serviceTrackerService.handle(template);
    }

    @MessageMapping(Endpoints.NODE)
    @SendTo(Topics.NODE)
    public void sendNodes() {
        nodeTrackerService.handle(template);
    }
}
