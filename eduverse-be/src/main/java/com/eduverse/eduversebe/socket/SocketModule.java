package com.eduverse.eduversebe.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer server;

    private static final Map<String, UUID> onlineUsers = new ConcurrentHashMap<>();

    public SocketModule(SocketIOServer server) {
        this.server = server;

        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("newUser", String.class, onNewUser());
    }

    private ConnectListener onConnected() {
        return (client) -> {
            log.info("Socket ID[{}]  Connected to socket", client.getSessionId().toString());
        };
    }

    private DisconnectListener onDisconnected() {
        return (client) -> {
            UUID sessionId = client.getSessionId();
            onlineUsers.values().removeIf(id -> id.equals(sessionId));
            log.info("Socket ID[{}]  Disconnected from socket", sessionId);
        };
    }

    private DataListener<String> onNewUser() {
        return (client, userId, ackSender) -> {
            log.info("User[{}] registered with Socket ID[{}]", userId, client.getSessionId());

            onlineUsers.put(userId, client.getSessionId());

            printOnlineUsers();
        };
    }


    public UUID getSessionIdByUserId(String userId) {
        return onlineUsers.get(userId);
    }

    public void sendEventToUser(String userId, String eventName, Object data) {
        UUID sessionId = onlineUsers.get(userId);
        if (sessionId != null) {
            SocketIOClient client = server.getClient(sessionId);
            if (client != null) {
                client.sendEvent(eventName, data);
            }
        }
    }

    private void printOnlineUsers() {
        log.info("Online Users: {}", onlineUsers.keySet());
    }
}
