package com.example.kuberneteswebsocketintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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
    public String sendPods() {
        //Message<Object> message, @Payload Object chatMessage
        podTrackerService.handle(template);
        return "";
    }

    @MessageMapping(Endpoints.SERVICE)
    @SendTo(Topics.SERVICE)
    public String sendServices() {
        serviceTrackerService.handle(template);
        return "";
    }

    @MessageMapping(Endpoints.NODE)
    @SendTo(Topics.NODE)
    public String sendNodes() {
        nodeTrackerService.handle(template);
        return "";
    }
}
