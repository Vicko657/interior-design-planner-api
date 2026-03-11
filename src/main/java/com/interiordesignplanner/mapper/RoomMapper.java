package com.interiordesignplanner.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomCreateDTO;
import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.room.RoomUpdateDTO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RoomMapper {
    @Autowired
    private ModelMapper modelMapper;

    public RoomDTO toDto(Room entity) {
        RoomDTO dto = modelMapper.map(entity, RoomDTO.class);
        return dto;
    }

    public Room toEntity(RoomCreateDTO roomCreateDTO) {
        Room entity = modelMapper.map(roomCreateDTO, Room.class);
        return entity;
    }

    public void updateEntity(RoomUpdateDTO roomUpdateDTO, Room room) {
        modelMapper.map(roomUpdateDTO, room);
    }

}
