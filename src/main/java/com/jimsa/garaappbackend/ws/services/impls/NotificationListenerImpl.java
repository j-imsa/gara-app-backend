package com.jimsa.garaappbackend.ws.services.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimsa.garaappbackend.configs.sse.SseEmitterManager;
import com.jimsa.garaappbackend.ws.model.dtos.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListenerImpl {

    private final SseEmitterManager sseEmitterManager;

    @RabbitListener(queues = "notifications.queue")
    public void handleNotification(byte[] rawMessage) {
        try {
            String json = new String(rawMessage, StandardCharsets.UTF_8);
            NotificationDto message = new ObjectMapper().readValue(json, NotificationDto.class);

            if (message.getTargetUserPublicId() != null) {
                sseEmitterManager.sendToUser(message.getTargetUserPublicId(), message);
            } else {
                sseEmitterManager.sendToAll(message);
            }
        } catch (Exception e) {
            log.error("Error handling notification: {}", e.getMessage(), e);
        }
    }

}
