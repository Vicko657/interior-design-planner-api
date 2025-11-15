package com.interiordesignplanner.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientCreateDTO;
import com.interiordesignplanner.client.ClientDTO;
import com.interiordesignplanner.client.ClientUpdateDTO;

@Component
public class ClientMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ClientDTO toDto(Client entity) {
        ClientDTO dto = modelMapper.map(entity, ClientDTO.class);
        return dto;
    }

    public Client toEntity(ClientCreateDTO clientCreateDTO) {
        Client entity = modelMapper.map(clientCreateDTO, Client.class);
        return entity;
    }

    public void updateEntity(ClientUpdateDTO clientUpdateDTO, Client client) {
        modelMapper.map(clientUpdateDTO, client);
    }

}
