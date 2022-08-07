package com.example.kuberneteswebsocketintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.example.kuberneteswebsocketintegration.service.tracking.NodeTrackingService;
import com.example.kuberneteswebsocketintegration.service.tracking.PodTrackingService;
import com.example.kuberneteswebsocketintegration.service.tracking.ServiceTrackingService;
import com.example.kuberneteswebsocketintegration.util.endpoint.Endpoints;

/**
 * Tracking controller, which fetches
 * data from local Kubernetes cluster
 */
@Controller
public class TrackingController {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NodeTrackingService nodeTrackerService;

    @Autowired
    private ServiceTrackingService serviceTrackerService;

    @Autowired
    private PodTrackingService podTrackerService;
   
    @SubscribeMapping(Endpoints.POD)
    public void sendPods() {
        podTrackerService.handle(template);
    }

    @SubscribeMapping(Endpoints.SERVICE)
    public void sendServices() {
        serviceTrackerService.handle(template);
    }

    @SubscribeMapping(Endpoints.NODE)
    public void sendNodes() {
        nodeTrackerService.handle(template);
    }
}
