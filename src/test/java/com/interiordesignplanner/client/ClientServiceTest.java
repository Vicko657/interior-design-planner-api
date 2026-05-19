package com.interiordesignplanner.client;

import java.util.ArrayList;
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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
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

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private DesignerService designerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Mock client service
    @InjectMocks
    private ClientService clientService;

    private Client client1, client2;

    private User user, admin;

    private Designer designer;

    @BeforeEach
    public void setUp() {

        // Added Client Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.clientMapper = new ClientMapper(modelMapper);

        clientService = new ClientService(clientRepository, clientMapper,
                authenticationService,
                designerService);

        user = new User();
        user.setId(1L);
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmailAddress("samwilliams@gmail.com");
        user.setPhoneNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("sam");
        user.setPassword(passwordEncoder.encode("huwa71egyw"));

        admin = new User();
        admin.setId(2L);
        admin.setFirstName("Grace");
        admin.setLastName("Smith");
        admin.setEmailAddress("gracesmith@gmail.com");
        admin.setPhoneNumber("07392648274");
        admin.setRoles(Roles.ADMIN);
        admin.setUsername("grace");
        admin.setPassword(passwordEncoder.encode("bchqwbbbqyw3"));

        designer = new Designer();
        designer.setId(1L);
        designer.setUser(user);

        // Created mock client tests
        client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmailAddress("jessicacook@gmail.com");
        client1.setPhoneNumber("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer);

        client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmailAddress("aprice@gmail.com");
        client2.setPhoneNumber("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");
        client2.setDesigner(designer);

    }

    /**
     * Tests for checking if Get all clients returns a page of clients
     */
    @Test
    @DisplayName("GetAllClients: Returns all of the clients in the database")
    public void testGetAllClients_ReturnsAllClients() {
        // Arrange: A page created with clients, pageable and mock Repository to test if
        // all
        // clients are returned

        Pageable pageable = PageRequest.of(0, 10);
        List<Client> clients = new ArrayList<>();
        clients.add(client1);
        clients.add(client2);

        Page<Client> mockPage = new PageImpl<>(clients);

        when(clientRepository.findAll(pageable))
                .thenReturn(mockPage);

        // Act: Query the service layer the if all clients are returned
        Page<ClientDTO> result = clientService.getAllClients(null, pageable);

        // Assert: Verifies that the result is not null and clients are retrieved
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);
        assertThat(result).extracting(ClientDTO::getId).containsExactly(1L, 2L);
        assertThat(result).extracting(ClientDTO::getFirstName).containsExactly("Jessica", "Alex");
        verify(clientRepository).findAll(any(
                Pageable.class));
        verifyNoMoreInteractions(clientRepository);

    }

    /**
     * Tests for checking if Get all clients returns one of client
     */
    @Test
    @DisplayName("GetAllClients: Returns one client in the database")
    public void testGetAllClients_ReturnsFilteredClient() {
        // Arrange: A page created with clients, pageable and mock Repository to test if
        // all
        // clients are returned

        String filter = "filter=firstName==Alex";

        Pageable pageable = PageRequest.of(2, 2);
        List<Client> clients = new ArrayList<>();
        clients.add(client2);

        Page<Client> mockPage = new PageImpl<>(clients);

        when(clientRepository.findAll(ArgumentMatchers.<Specification<Client>>any(), any(
                Pageable.class)))
                .thenReturn(mockPage);

        // Act: Query the service layer the if all clients are returned
        Page<ClientDTO> result = clientService.getAllClients(filter, pageable);

        // Assert: Verifies that the result is not null and projects are retrieved
        assertNotNull(result);
        assertEquals(result.getContent().size(), 1);
        assertThat(result).extracting(ClientDTO::getId).containsExactly(2L);
        assertThat(result).extracting(ClientDTO::getFirstName).containsExactly("Alex");
        verify(clientRepository).findAll(ArgumentMatchers.<Specification<Client>>any(), any(
                Pageable.class));
        verifyNoMoreInteractions(clientRepository);

    }

    /**
     * Tests for checking if Get all clients returns a empty page
     */
    @Test
    @DisplayName("GetAllClients: Returns empty page")
    public void testGetAllClients_ReturnsEmptyPage() {
        // Arrange: Empty page is created and Mock Repository to test if it returns a
        // empty page

        String filter = "filter=notes==null";

        Pageable pageable = PageRequest.of(2, 2);
        Page<Client> clients = Page.empty();
        when(clientRepository.findAll(ArgumentMatchers.<Specification<Client>>any(), any(Pageable.class)))
                .thenReturn(clients);

        // Act: Query the service layer if a empty page is returned
        Page<ClientDTO> result = clientService.getAllClients(filter, pageable);

        // Assert: Verifies that the result is empty
        assertThat(result).isEqualTo(clients);

    }

    /**
     * Tests for checking if Get clients returns a page of clients for the designer
     */
    @Test
    @DisplayName("GetClientsByDesigner: Returns all of the clients for designer")
    public void testGetClientsByDesigner_ReturnsAllClients() {
        // Arrange: A page created with clients, pageable and mock Repository to test if
        // all the designer's clients are returned

        ClientSummaryDTO clientSummaryDTO1 = new ClientSummaryDTO(1L, "Jessica Cook", "jessicacook@gmail.com",
                "07314708068", "33 Elm Street, London, N2R 652", 2L, "Prefers eco-friendly materials");

        ClientSummaryDTO clientSummaryDTO2 = new ClientSummaryDTO(2L, "Alex Price", "aprice@gmail.com", "07828096962",
                "249 The Grove, Reading, R84 J5N", 5L, "Needs child-friendly furniture");

        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientSummaryDTO> mockPage = new PageImpl<>(List.of(clientSummaryDTO1, clientSummaryDTO2));

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(clientRepository.findClientsByDesignerId(user.getId(), pageable))
                .thenReturn(mockPage);

        // Act: Query the service layer the if all the designer's clients are returned
        Page<ClientSummaryDTO> result = clientService.getClientsByDesigner(user.getUsername(), pageable);

        // Assert: Verifies that the result is not null and clients are retrieved
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);
        assertThat(result).extracting(ClientSummaryDTO::getId).containsExactly(1L, 2L);
        assertThat(result).extracting(ClientSummaryDTO::getNotes).containsExactly(
                "Prefers eco-friendly materials", "Needs child-friendly furniture");
        verify(clientRepository).findClientsByDesignerId(any(), any(
                Pageable.class));
        verifyNoMoreInteractions(clientRepository);

    }

    /**
     * Tests for checking if Get clients returns a page of clients for the designer
     */
    @Test
    @DisplayName("GetClientsByDesigner: Returns Empty Page")
    public void testGetClientsByDesigner_EmptyPage() {
        // Arrange: Empty page is created and Mock Repository to test if it returns a
        // empty page

        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientSummaryDTO> mockPage = Page.empty();

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(clientRepository.findClientsByDesignerId(user.getId(), pageable))
                .thenReturn(mockPage);

        // Act: Query the service layer if a empty page is returned
        Page<ClientSummaryDTO> result = clientService.getClientsByDesigner(user.getUsername(), pageable);

        // Assert: Verifies that the page is empty
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 0);
        verify(clientRepository).findClientsByDesignerId(any(), any(
                Pageable.class));
        verifyNoMoreInteractions(clientRepository);

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
        assertThat(result.getPhoneNumber()).isEqualTo("07314708068");
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
                "Loves minimalist design", designer);

        Client savedClient = new Client();
        savedClient.setFirstName("Simon");
        savedClient.setLastName("Harris");
        savedClient.setEmailAddress("harrissimon@gmail.com");
        savedClient.setPhoneNumber("07855443322");
        savedClient.setAddress("89 Riverbank Road, Birmingham, B23 O92");
        savedClient.setNotes("Loves minimalist design");
        savedClient.setDesigner(designer);

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // Act: Query the service layer the if client is there
        ClientDTO result = clientService.createClient(clientDTO, user.getUsername());

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
        updatedClient.setPhoneNumber("07829596562");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client2));
        when(clientRepository.save(client2)).thenReturn(client2);

        // Act: Query the service layer to return the client with the id and update the
        // client's details
        ClientDTO result = clientService.updateClient(clientId, updatedClient, user.getUsername());

        // Assert: Verifies that the client was updated
        assertNotNull(result);
        assertEquals(result.getPhoneNumber(), "07829596562");
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
            clientService.updateClient(clientId, updateClient, user.getUsername());
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
        verify(clientRepository, never()).delete(ArgumentMatchers.<Specification<Client>>any());

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(clientRepository);
    }

}
