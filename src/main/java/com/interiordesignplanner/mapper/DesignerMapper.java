package com.interiordesignplanner.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerProfileDTO;
import com.interiordesignplanner.designer.DesignerProfileUpdateDTO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DesignerMapper {

    @Autowired
    private ModelMapper modelMapper;

    public DesignerProfileDTO toDto(Designer entity) {

        DesignerProfileDTO dto = modelMapper.map(entity, DesignerProfileDTO.class);
        return dto;
    }

    public void updateProfile(DesignerProfileUpdateDTO designerUpdateDTO, Designer designer) {

        modelMapper.map(designerUpdateDTO, designer);

    }

}
