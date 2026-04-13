package com.example.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    private final int port;
    private final Server server;

    public GrpcServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService((BindableService) new UserServiceImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        logger.info("gRPC server started on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server...");
            GrpcServer.this.stop();
            System.err.println("Server stopped.");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServer server = new GrpcServer(9090);
        server.start();
        server.blockUntilShutdown();
    }
}