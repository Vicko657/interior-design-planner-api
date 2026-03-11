package com.interiordesignplanner.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectCreateDTO;
import com.interiordesignplanner.project.ProjectDTO;
import com.interiordesignplanner.project.ProjectUpdateDTO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProjectMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProjectDTO toDto(Project entity) {
        ProjectDTO dto = modelMapper.map(entity, ProjectDTO.class);
        return dto;
    }

    public Project toEntity(ProjectCreateDTO projectCreateDTO) {
        Project entity = modelMapper.map(projectCreateDTO, Project.class);
        return entity;
    }

    public void updateEntity(ProjectUpdateDTO projectUpdateDTO, Project project) {
        modelMapper.map(projectUpdateDTO, project);
    }

}
