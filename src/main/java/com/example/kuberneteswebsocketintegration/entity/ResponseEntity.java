package com.example.kuberneteswebsocketintegration.entity;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Entity used for representing the data
 * returned by Kubernetes cluster
 */
@Data
public class ResponseEntity<T> {

    /**
     * Enum used to represent kind of
     * resource used in response of Kubernetes cluster 
     */
    @AllArgsConstructor
    public static enum Kind {
        PodList("PodList"),
        ServiceList("ServiceList"),
        NodeList("NodeList");

        public String kind;

        public String toString(){
            return this.kind;
        }
    }

    public Kind kind;
    public T content;
}
