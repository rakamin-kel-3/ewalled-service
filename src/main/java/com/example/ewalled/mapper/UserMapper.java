package com.example.ewalled.mapper;

import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDto.Response toResponse(User user);
}
