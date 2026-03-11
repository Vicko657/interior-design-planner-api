package com.interiordesignplanner.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectDTO;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomDTO;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Converts Client Id and Room Id to Full Name and Room Type when mapped
        mapper.createTypeMap(Project.class, ProjectDTO.class).setPostConverter(convert -> {
            Project source = convert.getSource();
            ProjectDTO destination = convert.getDestination();
            if (source.getClient() != null) {
                destination.setClientName(source.getClient().getFirstName() + " " + source.getClient().getLastName());
            }
            if (source.getRoom() != null) {
                destination.setRoom(source.getRoom().getType());
            }
            return destination;
        });

        // Converts Project Id to Project Name when mapped
        mapper.createTypeMap(Room.class, RoomDTO.class).setPostConverter(convert -> {
            Room source = convert.getSource();
            RoomDTO destination = convert.getDestination();
            if (source.getProject() != null) {
                destination.setProjectName((source.getProject().getProjectName()));
            }
            return destination;
        });
        return mapper;
    }
}
