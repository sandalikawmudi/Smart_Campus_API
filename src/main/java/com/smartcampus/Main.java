package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static HttpServer startServer() {
        // Automatically scans for resources and providers at com.smartcampus
        final ResourceConfig rc = new ResourceConfig().packages("com.smartcampus");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        // Shutdown hook: ensures port 8080 is released even if stopped via
        // NetBeans stop button, Ctrl+C, or any abnormal JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdownNow();
        }));

        System.out.println(String.format("Jersey app started at %s", BASE_URI));
        System.out.println("Press Ctrl+C or stop the process to shut down.");
        try {
            Thread.currentThread().join(); // Keep main thread alive indefinitely
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt flag
        }
    }
}
