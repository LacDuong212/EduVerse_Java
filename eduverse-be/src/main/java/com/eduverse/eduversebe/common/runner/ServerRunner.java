package com.eduverse.eduversebe.common.runner;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        server.start();
        log.info("Socket.IO Server started on port {}", server.getConfiguration().getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            log.info("Socket.IO Server stopped");
        }));
    }
}
