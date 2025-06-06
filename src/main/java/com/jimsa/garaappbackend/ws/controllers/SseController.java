package com.jimsa.garaappbackend.ws.controllers;

import com.jimsa.garaappbackend.configs.sse.SseEmitterManager;
import com.jimsa.garaappbackend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterManager sseEmitterManager;
    private final AuthenticationUtil authUtil;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication authentication, HttpServletResponse response) {

        // Prevent caching in browsers
        response.setHeader("Cache-Control", "no-store");

        // Get user PublicID using the utility
        String userPublicID = authUtil.getPublicId(authentication);

        return sseEmitterManager.createEmitter(userPublicID);

    }

}
