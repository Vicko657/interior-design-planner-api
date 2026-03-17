package com.interiordesignplanner.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserCreateDTO;
import com.interiordesignplanner.authentication.UserDTO;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO toDto(User entity) {
        UserDTO dto = modelMapper.map(entity, UserDTO.class);
        return dto;
    }

    public User toEntity(UserCreateDTO userCreateDTO) {
        User entity = modelMapper.map(userCreateDTO, User.class);
        return entity;
    }

}
