package cds.fileuploadproject;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private final Map<String, StompSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        String sessionId = getSessionIdFromHeaders(connectedHeaders);
        sessions.put(sessionId, session);
    }

    private String getSessionIdFromHeaders(StompHeaders headers) {
        List<String> sessionIdHeaders = headers.get("message");
        if (sessionIdHeaders != null && !sessionIdHeaders.isEmpty()) {
            return sessionIdHeaders.get(0);
        }
        return null;
    }

    public StompSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void sendToAllClients(String destination, Object payload) {
        sessions.values().forEach(session -> session.send(destination, payload));
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

}

