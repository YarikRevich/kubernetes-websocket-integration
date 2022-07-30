package com.example.kuberneteswebsocketintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping(Endpoints.TRACKING)
public class TrackingController {
    @Autowired
    private NodeTrackingService nodeTrackerService;

    @Autowired
    private ServiceTrackingService serviceTrackerService;

    @Autowired
    private PodTrackingService podTrackerService;

    /**
     * Fetches node data from Kubernetes cluster
     */
    @MessageMapping(Endpoints.NODE)
    @SendTo(Topics.NODE)
    public void sendNodes(){
        nodeTrackerService.handle();
    }

    /**
     * Fetches service data from Kubernetes cluster
     */
    @MessageMapping(Endpoints.SERVICE)
    @SendTo(Topics.SERVICE)
    public void sendService(){
        serviceTrackerService.handle();
    }

    /**
     * Fetches pod data from Kubernetes cluster
     */
    @MessageMapping(Endpoints.POD)
    @SendTo(Topics.POD)
    public void sendPods(){
        podTrackerService.handle();
    }
}
