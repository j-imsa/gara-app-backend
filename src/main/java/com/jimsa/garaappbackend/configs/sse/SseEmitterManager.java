package com.jimsa.garaappbackend.configs.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterManager {

    // Map of connected clients: userPublicId -> SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String userPublicId) {

        log.debug("Creating new SseEmitter for user: {}", userPublicId);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(userPublicId, emitter);

        emitter.onCompletion(() -> emitters.remove(userPublicId));
        emitter.onTimeout(() -> emitters.remove(userPublicId));
        emitter.onError((e) -> emitters.remove(userPublicId));

        return emitter;
    }

    public void sendToUser(String userPublicId, Object data) {
        SseEmitter emitter = emitters.get(userPublicId);
        if (emitter != null) {
            try {
                emitter.send(data);
                log.debug("Sent SSE data to user: {}", userPublicId);
            } catch (IOException e) {
                log.warn("Error sending SSE data to user: {}", userPublicId, e);
                emitters.remove(userPublicId);
            }
        }
    }

    public void sendToAll(Object data) {
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            try {
                entry.getValue().send(data);
            } catch (IOException e) {
                log.warn("Error sending SSE data to user: {}", entry.getKey(), e);
                emitters.remove(entry.getKey());
            }
        }
        log.debug("Sent SSE data to all users");
    }
}
