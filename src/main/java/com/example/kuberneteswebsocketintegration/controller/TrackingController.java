package com.example.kuberneteswebsocketintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Endpoint;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
public class TrackingController {
    @Autowired
    private SimpMessagingTemplate template;

    // @Autowired
    // private SimpMessagingTemplate template;

    @Autowired
    private NodeTrackingService nodeTrackerService;

    @Autowired
    private ServiceTrackingService serviceTrackerService;

    @Autowired
    private PodTrackingService podTrackerService;

    /**
     * Fetches node data from Kubernetes cluster
     */
    // @MessageMapping(Endpoints.NODE)
    // public void sendNodes() {
    // nodeTrackerService.handle();
    // }

    /**
     * Fetches service data from Kubernetes cluster
     */
    // @MessageMapping(Endpoints.SERVICE)
    // public void sendService() {
    // serviceTrackerService.handle();
    // }

    // @Autowired
    // private MessageSendingOperations<String> template;

    /**
     * Fetches pod data from Kubernetes cluster
     */
    // @RequestMapping(path=Endpoints.POD, method=RequestMethod.POST)
    // @EnableScheduling
    // @Controller
    // class GreetingController {

    //     @Scheduled(fixedRate = 1000)
    //     public void sendPods() {
    //         System.out.println("SCHEDULING");
    //         this.template.convertAndSend(Topics.POD, "Hello");
    //     }

    // }

    @MessageMapping(Endpoints.POD)
    public void sendPods() {
        podTrackerService.handle(template);
    }

    // @MessageMapping("/welcome")
    // @SendTo("/topic/greetings")
    // public String greeting(String payload) {
    // System.out.println("Generating new greeting message for " + payload);
    // return "Hello, " + payload + "!";
    // }
}
