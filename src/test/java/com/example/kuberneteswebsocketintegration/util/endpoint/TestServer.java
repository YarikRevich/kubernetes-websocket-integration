package com.example.kuberneteswebsocketintegration.util.endpoint;

/**
 * Contains all the utility methods used for testing
 * with a help of a test server
 */
public class TestServer {
    /**
     * @param port the port of the local server
     * @return an URL in a string format of to connect
     *         to a local server
     */
    public static String getServerEndpoint(int port){
        return String.format("ws://localhost:%d/ws");
    }

    /**
     * @param endpoint endpoint to send data to
     * @return formatted destination path
     */
    public static String getDestinationPath(String endpoint){
        return String.format("/app/%s", endpoint);
    }
}
