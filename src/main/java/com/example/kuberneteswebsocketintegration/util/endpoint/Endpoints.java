package com.example.kuberneteswebsocketintegration.util.endpoint;

public class Endpoints {
    public final static String TRACKING = "tracking";
    
    public final static String SERVICE = "service";
    public final static String POD = "pod";
    public final static String NODE = "node";

    public final static String APPLICATION_DESTINATION = "/app";

    /**
     * Formats endpoint for application access using
     * controller endpoint
     * @param controllerEndpoint controller endpoint
     * @return endpoint for application access
     */
    public String formatApplicationDestinationEndpoint(String controllerEndpoint){
        return String.format("%s/%s", APPLICATION_DESTINATION, controllerEndpoint);
    }
}
