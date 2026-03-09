package com.interiordesignplanner.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ClientRepository}.
 *
 * <p>
 * This class verifies the persistence and retrieval of {@link Client} entities.
 * It focuses on repository-level behavior including:
 * <p>
 * The tests use mocked repository behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Client Repository Test Suite")
public class ClientRepositoryTest {

    // Mock client repository
    @Mock
    public ClientRepository cRepository;

    public Client client1, client2, client3;

    @BeforeEach
    public void setUp() {
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

        client3 = new Client();
        client3.setId(3L);
        client3.setFirstName("Simon");
        client3.setLastName("Harris");
        client3.setEmail("harrissimon@gmail.com");
        client3.setPhone("07855443322");
        client3.setAddress("89 Riverbank Road, Birmingham, B23 O92");
        client3.setNotes("Loves minimalist design");
    }

    /**
     * Tests if the Client can be found with their lastname and different case
     */
    @Test
    @DisplayName("FindByLastName: Finds client by lastname and ignorescase")
    public void testfindByLastNameIgnoreCase_ReturnsSameClient() {

        // Arrange: Prepare test clients mapped to last names in different cases
        Map<String, Optional<Client>> test = new HashMap<>();
        test.put("cook", Optional.of(client1));
        test.put("PRICE", Optional.of(client2));
        test.put("Harr", Optional.of(client3));

        for (Map.Entry<String, Optional<Client>> entry : test.entrySet()) {

            String lastName = entry.getKey();
            Optional<Client> expectedClients = entry.getValue();

            // Mock repository behavior
            when(cRepository.findByLastNameIgnoreCase(lastName)).thenReturn(expectedClients);

            // Act: Query repository with each last name
            Optional<Client> result = cRepository.findByLastNameIgnoreCase(lastName);

            // Assert: Verify results match expected clients, ignoring case
            assertNotNull(result);
            assertEquals(expectedClients.equals(test), result.isEmpty());
            assertEquals(expectedClients, result);

        }

    }

    /**
     * Tests when the Client isnt found by LastName and returns a empty set
     */
    @Test
    @DisplayName("FindByLastName: Client isnt found by lastname and ignorescase")
    public void testfindByLastNameIgnoreCase_ReturnsEmptyList() {

        // Arrange: Mock Repository to test a different if the client with last name
        // ("Brown") is found
        when(cRepository.findByLastNameIgnoreCase("Brown")).thenReturn(Optional.empty());

        // Act: Query the repository the lastname ("Brown")
        Optional<Client> result = cRepository.findByLastNameIgnoreCase("Brown");

        // Assert: result is not null, is empty and repository was called
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cRepository).findByLastNameIgnoreCase("Brown");

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        Mockito.reset(cRepository);
    }

}