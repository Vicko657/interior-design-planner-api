package com.interiordesignplanner.client;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName(value = "Client Controller Test Suite")
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    // Converts the clientDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerRepository designerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ClientUpdateDTO clientUpdateDTO;

    private Designer designer;

    @BeforeEach
    @Transactional
    void setUp() {

        clientRepository.deleteAll();
        designerRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmail("samwilliams@gmail.com");
        user.setMobileNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("sam");
        user.setPassword(passwordEncoder.encode("huwa71egyw"));
        userRepository.save(user);

        User admin = new User();
        admin.setFirstName("Grace");
        admin.setLastName("Smith");
        admin.setEmail("gracesmith@gmail.com");
        admin.setMobileNumber("07392648274");
        admin.setRoles(Roles.ADMIN);
        admin.setUsername("grace");
        admin.setPassword(passwordEncoder.encode("bchqwbbbqyw3"));
        userRepository.save(admin);

        designer = new Designer();
        designer.setUser(user);
        designerRepository.save(designer);

        Client client1 = new Client();
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmail("jessicacook@gmail.com");
        client1.setPhone("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer);
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmail("aprice@gmail.com");
        client2.setPhone("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");
        client2.setDesigner(designer);

        clientRepository.save(client1);
        clientRepository.save(client2);

        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.setId(1L);
        clientDTO1.setFirstName("Jessica");
        clientDTO1.setLastName("Cook");
        clientDTO1.setEmail("jessicacook@gmail.com");
        clientDTO1.setPhone("07314708068");
        clientDTO1.setAddress("33 Elm Street, London, N2R 652");
        clientDTO1.setNotes("Prefers eco-friendly materials");

        ClientDTO clientDTO2 = new ClientDTO();
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
    @WithMockUser(roles = "ADMIN")
    void testGetAllClients() throws Exception {

        mockMvc.perform(get("/api/admin/clients?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].firstName", is("Jessica")))
                .andExpect(jsonPath("$.content[1].lastName", is("Price")));

    }

    @Test
    @DisplayName("GetAllClients: Should return one Client")
    @WithMockUser(roles = "ADMIN")
    void testGetAllClients_ReturnOneClient() throws Exception {

        mockMvc.perform(get("/api/admin/clients?filter=firstName==Alex")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Alex")));

    }

    @Test
    @DisplayName("GetClients: Should return all Clients")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetClients() throws Exception {

        mockMvc.perform(get("/api/clients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName").value("Jessica Cook"))
                .andExpect(jsonPath("$.content[1].email", is("aprice@gmail.com")));

    }

    @Test
    @DisplayName("GetClientById: Should return a Client")
    @WithMockUser(roles = { "ADMIN" })
    void testGetClientById() throws Exception {
        // Given

        // When/Then
        mockMvc.perform(get("/api/admin/clients/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("jessicacook@gmail.com")))
                .andExpect(jsonPath("$.notes", is("Prefers eco-friendly materials")));
    }

    @Test
    @DisplayName("GetClientById: Client Not Found")
    @WithMockUser(roles = { "ADMIN" })
    void testGetClientById_NotFound() throws Exception {
        // Given
        // When/Then
        mockMvc.perform(get("/api/admin/clients/199")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Client is not found with clientId: 199")));

    }

    @Test
    @DisplayName("CreateClient: Client is created")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCreateClient() throws Exception {

        // Given
        ClientCreateDTO client = new ClientCreateDTO();
        client.setFirstName("Jessica");
        client.setLastName("Cook");
        client.setEmail("jessicacook@gmail.com");
        client.setPhone("07314708068");
        client.setAddress("33 Elm Street, London, N2R 652");
        client.setNotes("Prefers eco-friendly materials");
        client.setDesigner(designer);

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)));

    }

    @Test
    @DisplayName("CreateClient: Missing FirstName")
    @WithMockUser(username = "sam", roles = { "DESIGNER" })
    void testCreateClient_ValidationFailure() throws Exception {
        // Given
        ClientCreateDTO client2 = new ClientCreateDTO();
        client2.setLastName("Cook");
        client2.setEmail("jessicacook@gmail.com");
        client2.setPhone("07314708068");
        client2.setAddress("33 Elm Street, London, N2R 652");
        client2.setNotes("Prefers eco-friendly materials");

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName", is("First name is required")));

    }

    @Test
    @DisplayName("UpdateClient: Client's firstName is updated")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUpdateClient() throws Exception {
        // Given
        clientUpdateDTO = new ClientUpdateDTO();
        clientUpdateDTO.setFirstName("Alexandra");

        // When/Then
        mockMvc.perform(put("/api/clients/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Alexandra")));

    }

    @Test
    @DisplayName("UpdateClient: Client not found")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUpdateClient_NotFound() throws Exception {
        // Given

        clientUpdateDTO = new ClientUpdateDTO();
        clientUpdateDTO.setPhone("07339204531");

        // When/Then
        mockMvc.perform(put("/api/clients/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Client is not found with clientId: 6")));

    }

    @Test
    @DisplayName("DeleteClient: Client is deleted")
    @WithMockUser(roles = { "ADMIN" })
    void testDeleteClient() throws Exception {

        // When/Then
        mockMvc.perform(delete("/api/admin/clients/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("ReassignDesigner: Client is deleted")
    @WithMockUser(roles = { "ADMIN" })
    void testReassignDesigner() throws Exception {

        // When/Then
        mockMvc.perform(delete("/api/admin/clients/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

}
