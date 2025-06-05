package com.jimsa.garaappbackend.utils.mappers;

import com.jimsa.garaappbackend.ws.model.dtos.UserDto;
import com.jimsa.garaappbackend.ws.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "encryptedPassword", ignore = true)
    UserEntity toEntity(UserDto userDto);

    @Mapping(target = "password", ignore = true)
    UserDto toDto(UserEntity userEntity);

}
