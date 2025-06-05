package com.jimsa.garaappbackend.ws.services;

import com.jimsa.garaappbackend.ws.model.dtos.UserDto;

public interface UserService {
    UserDto getUserDetails(String publicId);
}
