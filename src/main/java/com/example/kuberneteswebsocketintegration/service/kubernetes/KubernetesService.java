package com.example.kuberneteswebsocketintegration.service.kubernetes;

import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper for fetching data from Kubernetes cluster
 */
@Slf4j
@Service
public class KubernetesService {
    /**
     * Fetches data of all pods in Kubernetes cluster
     * @return output of all pods in YAML format
     */
    public String getAllPods() {
        CoreV1Api api = new CoreV1Api();
        V1PodList list = null;
        try {
            list = api.listPodForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }
        return list.toString();
    }

    /**
     * Fetches data of all services in Kubernetes cluster
     * @return output of all services in YAML format
     */
    public V1ServiceList getAllServices(){
        CoreV1Api api = new CoreV1Api();
        V1ServiceList list = null;
        try {
            list = api.listServiceForAllNamespaces(false, null, null, null, 0, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }
        // for (V1Service service : list.getItems()){
        //     service.getSpec().getPorts().get(0).getTargetPort()
        // }
        return list;
    }

    /**
     * Fetches data of all nodes in Kubernetes cluster
     * @return output of all nodes in YAML format
     */
    public String getAllNodes(){
        CoreV1Api api = new CoreV1Api();
        V1NodeList list = null;
        try {
            list = api.listNode(null, false, null, null, null, null, null, null, 0, false);
        } catch (ApiException e) {
            log.error("Kubernetes API is not accessible");
        }
        return list.toString();
    }
}
