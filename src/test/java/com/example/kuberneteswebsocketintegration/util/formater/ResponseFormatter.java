package com.example.kuberneteswebsocketintegration.util.formater;

import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;

import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1NodeList;

/**
 * Formatters used for testing trackers
 */
public class ResponseFormatter {
    /**
     * Formats response for testing
     * 
     * @param podList list of pods received from Kubernetes cluster
     * @return response entity of V1PodList received from Kubernetes cluster
     */
    public static ResponseEntity<V1PodList> formatResponseFromPodList(V1PodList podList) {
        ResponseEntity<V1PodList> response = new ResponseEntity<>();
        response.setKind(ResponseEntity.Kind.PodList);
        response.setContent(podList);

        return response;
    }

    /**
     * Formats response for testing
     * 
     * @param nodeList list of nodes received from Kubernetes cluster
     * @return response entity of V1NodeList received from Kubernetes cluster
     */
    public static ResponseEntity<V1NodeList> formatResponseFromNodeList(V1NodeList nodeList) {
        ResponseEntity<V1NodeList> response = new ResponseEntity<>();
        response.setKind(ResponseEntity.Kind.NodeList);
        response.setContent(nodeList);

        return response;
    }

    /**
     * Formats response for testing
     * 
     * @param serviceList list of services received from Kubernetes cluster
     * @return response entity of V1ServiceList received from Kubernetes cluster
     */
    public static ResponseEntity<V1ServiceList> formatResponseFromServiceList(V1ServiceList serviceList) {
        ResponseEntity<V1ServiceList> response = new ResponseEntity<>();
        response.setKind(ResponseEntity.Kind.ServiceList);
        response.setContent(serviceList);

        return response;
    }
}
