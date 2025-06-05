package com.jimsa.garaappbackend.ws.controllers;

import com.jimsa.garaappbackend.ws.model.dtos.ResponseDto;
import com.jimsa.garaappbackend.ws.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.APP_SECURITY_AUTHORIZATION;

@RestController
@RequestMapping(path = "/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{public_id}")
    public ResponseEntity<ResponseDto> getUserDetails(@PathVariable("public_id") String publicId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .action(true)
                        .result(userService.getUserDetails(publicId))
                        .build()
                );
    }

}
