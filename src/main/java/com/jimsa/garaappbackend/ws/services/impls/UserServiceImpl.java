package com.jimsa.garaappbackend.ws.services.impls;

import com.jimsa.garaappbackend.ws.model.dtos.UserDto;
import com.jimsa.garaappbackend.ws.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    @Override
    public UserDto getUserDetails(String publicId) {
        return null;
    }

}
