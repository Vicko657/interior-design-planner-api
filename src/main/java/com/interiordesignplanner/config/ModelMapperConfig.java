package com.interiordesignplanner.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientDTO;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerProfileDTO;
import com.interiordesignplanner.designer.DesignerProfileUpdateDTO;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectDTO;
import com.interiordesignplanner.projectsummary.ProjectSummaryDTO;
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

        // Converts Client Id to Designer Full Name and TotalProjects when mapped
        mapper.createTypeMap(Client.class, ClientDTO.class).setPostConverter(convert -> {
            Client source = convert.getSource();
            ClientDTO destination = convert.getDestination();
            if (source.getProjects() != null) {
                destination.setTotalProjects((source.getProjects().size()));
            }

            if (source.getDesigner() != null) {
                destination.setDesigner(source.getDesigner().getUser().getFirstName() + " " + source.getDesigner()
                        .getUser().getLastName());
            }
            return destination;
        });

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

        // Converts User Id to Designer Name, EmailAddress and PhoneNumber when mapped
        mapper.createTypeMap(Designer.class, DesignerProfileDTO.class).setPostConverter(convert -> {
            Designer source = convert.getSource();
            DesignerProfileDTO destination = convert.getDestination();
            if (source.getUser() != null) {
                destination.setName((source.getUser().getFirstName() + " " + source.getUser().getLastName()));

                destination.setFirstName((source.getUser().getFirstName()));

                destination.setLastName((source.getUser().getLastName()));

                destination.setEmailAddress((source.getUser().getEmailAddress()));

                destination.setPhoneNumber((source.getUser().getPhoneNumber()));
            }

            return destination;
        });

        // Converts User Id to Designer FirstName, LastName, EmailAddress and
        // PhoneNumber when mapped
        mapper.createTypeMap(DesignerProfileUpdateDTO.class, Designer.class).setPostConverter(convert -> {
            DesignerProfileUpdateDTO source = convert.getSource();
            Designer destination = convert.getDestination();

            if (source.getFirstName() != null) {
                destination.getUser().setFirstName((source.getFirstName()));
            }
            if (source.getLastName() != null) {
                destination.getUser().setLastName((source.getLastName()));
            }
            if (source.getEmailAddress() != null) {
                destination.getUser().setEmailAddress((source.getEmailAddress()));
            }
            if (source.getPhoneNumber() != null) {
                destination.getUser().setPhoneNumber((source.getPhoneNumber()));
            }

            return destination;
        });

        // Converts Project Id to Client and Room details when mapped
        mapper.createTypeMap(Project.class, ProjectSummaryDTO.class).setPostConverter(convert -> {
            Project source = convert.getSource();
            ProjectSummaryDTO destination = convert.getDestination();

            if (source.getClient() != null) {
                destination.setClientId(source.getClient().getId());
                destination.setClientName(source.getClient().getFirstName() + " " + source.getClient()
                        .getLastName());
            }

            if (source.getRoom() != null) {
                destination.setHeight(source.getRoom().getHeight());
                destination.setLength(source.getRoom().getLength());
                destination.setWidth(source.getRoom().getWidth());
                destination.setUnit(source.getRoom().getUnit());
                destination.setRoomId(source.getRoom().getId());
                destination.setRoom(source.getRoom().getType());
            }

            return destination;
        });
        return mapper;
    }
}
