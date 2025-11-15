package com.interiordesignplanner.client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.interiordesignplanner.exceptions.ClientNotFoundException;
import com.interiordesignplanner.mapper.ClientMapper;
import com.interiordesignplanner.project.ProjectRepository;

/**
 * Unit tests for {@link ClientService}.
 *
 * <p>
 * Verifies client creation, retrieval, updating, and deletion logic.
 * Ensures that validation rules and exception handling (such as
 * {@code ClientNotFoundException}) work as expected.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Client Service Test Suite")
public class ClientServiceTest {

    // Mock client repository
    @Mock
    public ClientRepository clientRepository;

    // Client mapper
    private ClientMapper clientMapper;

    // Mock project repository
    @Mock
    private ProjectRepository projectRepository;

    // Mock client service
    @InjectMocks
    private ClientService clientService;

    private Client client1, client2;

    @BeforeEach
    public void setUp() {

        // Added Client Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.clientMapper = new ClientMapper(modelMapper);

        clientService = new ClientService(clientRepository, clientMapper, projectRepository);

        // Created mock client tests
        client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmail("jessicacook@gmail.com");
        client1.setPhone("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");

        client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmail("aprice@gmail.com");
        client2.setPhone("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");

    }

    /**
     * Tests for checking if Get all clients returns a list of projects
     */
    @Test
    @DisplayName("GetAllClients: Returns all of the clients in the database")
    public void testGetAllClients_ReturnsAllClients() {
        // Arrange: A list created with clients and mock Repository to test if all
        // clients are returned

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        // Act: Query the service layer the if all clients are returned
        List<ClientDTO> result = clientService.getAllClients();

        // Assert: Verifies that the result is not null and projects are retrieved
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertThat(result).extracting(ClientDTO::getId).containsExactly(1L, 2L);
        assertThat(result).extracting(ClientDTO::getFirstName).containsExactly("Jessica", "Alex");
        verify(clientRepository).findAll();
        verifyNoMoreInteractions(clientRepository);

    }

    /**
     * Tests for checking if Get all clients returns a empty list
     */
    @Test
    @DisplayName("GetAllClients: Returns empty list")
    public void testGetAllClients_ReturnsEmptyList() {
        // Arrange: Empty list is created and Mock Repository to test if it returns a
        // empty list
        List<Client> clients = Collections.emptyList();
        when(clientRepository.findAll()).thenReturn(clients);

        // Act: Query the service layer the if a empty list is returned
        List<ClientDTO> result = clientService.getAllClients();

        // Assert: Verifies that the result is empty
        assertThat(result).isEqualTo(clients);

    }

    /**
     * Tests for when the client is found with the client id
     */
    @Test
    @DisplayName("GetClient: Returns client by ID")
    public void testGetClient_ReturnsClient() {
        // Arrange: Sets the clientId and mocks the repository
        Long clientId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client1));

        // Act: Query the service layer to return the client with the id
        ClientDTO result = clientService.getClientById(clientId);

        // Assert: Verifies that the result is not null and a client with the same Id is
        // returned
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(clientId);
        assertThat(result.getPhone()).isEqualTo("07314708068");
    }

    /**
     * Tests for when the Client is not found, returns a empty set and throws a
     * ClientNotFoundException
     */
    @Test
    @DisplayName("GetClient: Client ID is not found")
    public void testGetClient_ReturnsNotFound() {
        // Arrange: Set the clientId and mock the repository
        Long clientId = 3L;
        String errorMessage = "Client is not found with " + "clientId" + ": " + clientId;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown
        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClientById(clientId);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    /**
     * Tests for creating a new Client successfully
     */
    @Test
    @DisplayName("CreateClient: Adds a new Client")
    public void testCreateClient_ReturnsCreated() {
        // Arrange: Mock Repository to test if a new client has been created
        ClientCreateDTO clientDTO = new ClientCreateDTO(
                "Simon",
                "Harris",
                "harrissimon@gmail.com",
                "07855443322",
                "89 Riverbank Road, Birmingham, B23 O92",
                "Loves minimalist design");

        Client savedClient = new Client();
        savedClient.setId(3L);
        savedClient.setFirstName("Simon");
        savedClient.setLastName("Harris");
        savedClient.setEmail("harrissimon@gmail.com");
        savedClient.setPhone("07855443322");
        savedClient.setAddress("89 Riverbank Road, Birmingham, B23 O92");
        savedClient.setNotes("Loves minimalist design");

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // Act: Query the service layer the if client is there
        ClientDTO result = clientService.createClient(clientDTO);

        // Assert: Verifies that the result is not null and client has been created
        assertNotNull(result);
        assertThat(result).extracting(ClientDTO::getFirstName).isEqualTo("Simon");
        verify(clientRepository, times(1)).save(any(Client.class));

    }

    /**
     * Tests for updating a client
     */
    @Test
    @DisplayName("UpdateClient: Updates client details")
    public void testUpdateClient_ReturnsUpdated() {
        // Arrange: Sets the clientId and mocks the repository
        Long clientId = 2L;

        // Updated Telephone number
        ClientUpdateDTO updatedClient = new ClientUpdateDTO();
        updatedClient.setPhone("07829596562");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client2));
        when(clientRepository.save(client2)).thenReturn(client2);

        // Act: Query the service layer to return the client with the id and update the
        // client's details
        ClientDTO result = clientService.updateClient(clientId, updatedClient);

        // Assert: Verifies that the client was updated
        assertNotNull(result);
        assertEquals(result.getPhone(), "07829596562");
        verify(clientRepository).findById(clientId);

    }

    /**
     * Tests for updating a client and the client is not found
     */
    @Test
    @DisplayName("UpdateClient: Client ID is not found")
    public void testUpdateClient_ReturnsNotFound() {
        // Arrange: Sets the clientId and mocks the repository
        Long clientId = 2L;
        String errorMessage = "Client is not found with " + "clientId" + ": " + clientId;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientUpdateDTO updateClient = new ClientUpdateDTO();
        updateClient.setFirstName("John");

        // Act: Queries if the exception is thrown if client is not found when updating
        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> {
            clientService.updateClient(clientId, updateClient);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).save(null);

    }

    /**
     * Tests for deleting a client
     */
    @Test
    @DisplayName("DeleteClient: Deletes client details")
    public void testDeleteClient_ReturnsDeleted() {
        // Arrange: Sets the clientId and mocks the repository
        Long clientId = 2L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client2));

        // Act: Query the service layer to return the client with the id and delete the
        // client
        clientService.deleteClient(clientId);

        // Assert: Verifies that the client was deleted and is not found
        verify(clientRepository).delete(client2);
        verify(clientRepository).findById(clientId);

    }

    /**
     * Tests for deleting a client and the client is not found
     */
    @Test
    @DisplayName("DeleteClient: Client ID is not found")
    public void testDeleteClient_ReturnsNotFound() {
        // Arrange: Sets the clientId sand mocks the repository
        Long clientId = 2L;
        String errorMessage = "Client is not found with " + "clientId" + ": " + clientId;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown if client is not found when deleting
        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> {
            clientService.deleteClient(clientId);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).delete(any());

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(clientRepository);
    }

}
