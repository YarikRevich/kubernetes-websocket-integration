package com.example.kuberneteswebsocketintegration.util.formater;

import com.example.kuberneteswebsocketintegration.entity.ResponseEntity;

import io.kubernetes.client.openapi.models.V1PodList;

public class ResponseFormater {
    public static ResponseEntity<V1PodList> formatResponseFromPodList(V1PodList podList) {
        ResponseEntity<V1PodList> response = new ResponseEntity<>();
        response.setKind(ResponseEntity.Kind.PodList);
        response.setContent(podList);

        return response;
    }
}
