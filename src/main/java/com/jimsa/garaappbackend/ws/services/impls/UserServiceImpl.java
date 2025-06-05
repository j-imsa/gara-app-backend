package com.jimsa.garaappbackend.ws.services.impls;

import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import com.jimsa.garaappbackend.utils.mappers.UserMapper;
import com.jimsa.garaappbackend.ws.model.dtos.UserDto;
import com.jimsa.garaappbackend.ws.model.entities.UserEntity;
import com.jimsa.garaappbackend.ws.repositories.UserRepository;
import com.jimsa.garaappbackend.ws.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserDetails(String publicId) {

        log.info("Get user details for public id: {}", publicId);

        try {
            Optional<UserEntity> userOptional = userRepository.findByPublicId(publicId);

            if (userOptional.isEmpty()) {
                log.warn("User not found with public id: {}", publicId);
                throw new AppServiceException("User not found", HttpStatus.NOT_FOUND);
            }

            UserEntity userEntity = userOptional.get();
            UserDto userDto = userMapper.toDto(userEntity);

            // Clear sensitive information from response
            userDto.setPassword(null);

            log.info("Successfully retrieved user details for public id: {}", publicId);

            return userDto;

        } catch (Exception e) {
            log.error("Error retrieving user details for public id: {}", publicId, e);
            throw new AppServiceException("Failed to retrieve user details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}