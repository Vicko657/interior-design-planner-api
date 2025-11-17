package com.interiordesignplanner.client;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.interiordesignplanner.exceptions.ClientNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClientController.class)
@DisplayName(value = "Client Controller Test Suite")
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Converts the ClientDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Mockito Bean replaces MockBean(depreciated)
    @MockitoBean
    private ClientService clientService;

    private ClientDTO clientDTO1, clientDTO2;

    private ClientCreateDTO clientCreateDTO, clientCreateDTO2;

    private ClientUpdateDTO clientUpdateDTO;

    @BeforeEach
    void setUp() {

        clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setFirstName("Jessica");
        clientCreateDTO.setLastName("Cook");
        clientCreateDTO.setEmail("jessicacook@gmail.com");
        clientCreateDTO.setPhone("07314708068");
        clientCreateDTO.setAddress("33 Elm Street, London, N2R 652");
        clientCreateDTO.setNotes("Prefers eco-friendly materials");

        clientDTO1 = new ClientDTO();
        clientDTO1.setId(1L);
        clientDTO1.setFirstName("Jessica");
        clientDTO1.setLastName("Cook");
        clientDTO1.setEmail("jessicacook@gmail.com");
        clientDTO1.setPhone("07314708068");
        clientDTO1.setAddress("33 Elm Street, London, N2R 652");
        clientDTO1.setNotes("Prefers eco-friendly materials");

        clientDTO2 = new ClientDTO();
        clientDTO2.setId(2L);
        clientDTO2.setFirstName("Alex");
        clientDTO2.setLastName("Price");
        clientDTO2.setEmail("aprice@gmail.com");
        clientDTO2.setPhone("07828096962");
        clientDTO2.setAddress("249 The Grove, Reading, R84 J5N");
        clientDTO2.setNotes("Needs child-friendly furniture");

    }

    @Test
    @DisplayName("GetAllClients: Should return all Clients")
    void testGetAllClients() throws Exception {

        // Given
        when(clientService.getAllClients()).thenReturn(List.of(clientDTO1, clientDTO2));

        // When/Then
        mockMvc.perform(get("/api/clients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jessica")))
                .andExpect(jsonPath("$[1].lastName", is("Price")));

        verify(clientService).getAllClients();
    }

    @Test
    @DisplayName("GetClientById: Should return a Client")
    void testGetClientById() throws Exception {
        // Given
        when(clientService.getClientById(1L)).thenReturn(clientDTO1);

        // When/Then
        mockMvc.perform(get("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("jessicacook@gmail.com")))
                .andExpect(jsonPath("$.notes", is("Prefers eco-friendly materials")));

        verify(clientService).getClientById(1L);
    }

    @Test
    @DisplayName("GetClientById: Client Not Found")
    void testGetClientById_NotFound() throws Exception {
        // Given
        when(clientService.getClientById(199L)).thenThrow(new ClientNotFoundException("clientId", 199L));

        // When/Then
        mockMvc.perform(get("/api/clients/199")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Client is not found with clientId: 199")));

        verify(clientService).getClientById(199L);
    }

    @Test
    @DisplayName("CreateClient: Client is created")
    void testCreateClient() throws Exception {
        // Given
        when(clientService.createClient(clientCreateDTO)).thenReturn(clientDTO1);

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(clientService).createClient(clientCreateDTO);
    }

    @Test
    @DisplayName("CreateClient: Missing FirstName")
    void testCreateClient_ValidationFailure() throws Exception {
        // Given
        clientCreateDTO2 = new ClientCreateDTO();
        clientCreateDTO2.setLastName("Cook");
        clientCreateDTO2.setEmail("jessicacook@gmail.com");
        clientCreateDTO2.setPhone("07314708068");
        clientCreateDTO2.setAddress("33 Elm Street, London, N2R 652");
        clientCreateDTO2.setNotes("Prefers eco-friendly materials");

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientCreateDTO2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("Client's firstName is required")));

        verify(clientService, never()).createClient(clientCreateDTO2);
    }

    @Test
    @DisplayName("UpdateClient: Client's firstName is updated")
    void testUpdateClient() throws Exception {
        // Given
        Long id = clientDTO2.getId();
        clientUpdateDTO = new ClientUpdateDTO();
        clientUpdateDTO.setFirstName("Alexandra");

        clientDTO2.setFirstName("Alexandra");

        when(clientService.updateClient(id, clientUpdateDTO)).thenReturn(clientDTO2);

        // When/Then
        mockMvc.perform(put("/api/clients/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Alexandra")));

        verify(clientService).updateClient(id, clientUpdateDTO);
    }

    @Test
    @DisplayName("UpdateClient: Client not found")
    void testUpdateClient_NotFound() throws Exception {
        // Given
        Long id = 6L;
        clientUpdateDTO = new ClientUpdateDTO();
        when(clientService.updateClient(id, clientUpdateDTO)).thenThrow(new ClientNotFoundException("clientId", id));

        // When/Then
        mockMvc.perform(put("/api/clients/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Client is not found with clientId: 6")));

        verify(clientService).updateClient(id, clientUpdateDTO);
    }

    @Test
    @DisplayName("DeleteClient: Client is deleted")
    void testDeleteClient() throws Exception {
        // Given
        Long id = 2L;
        doNothing().when(clientService).deleteClient(id);

        // When/Then
        mockMvc.perform(delete("/api/clients/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClient(id);
    }

}
