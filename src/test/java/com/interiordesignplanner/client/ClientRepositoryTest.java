package com.interiordesignplanner.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;

/**
 * Unit tests for {@link ClientRepository}.
 *
 * <p>
 * This class verifies the persistence and retrieval of {@link Client} entities.
 * It focuses on repository-level behavior including:
 * <p>
 * The tests use mocked repository behavior.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName(value = "Client Repository Test Suite")
public class ClientRepositoryTest {

    // Mock client repository
    @Autowired
    public ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerRepository designerRepository;

    public Client client1, client2, client3;

    public Designer designer1, designer2;

    public User user1, user2;

    @BeforeEach
    public void setUp() {

        clientRepository.deleteAll();
        designerRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setFirstName("Dove");
        user1.setLastName("White");
        user1.setEmailAddress("dovewhite@gmail.com");
        user1.setPhoneNumber("07223180736");
        user1.setRoles(Roles.DESIGNER);
        user1.setUsername("dovewhite");
        user1.setPassword("gsjgtq893x");

        user2 = new User();
        user2.setFirstName("Sasha");
        user2.setLastName("Walker");
        user2.setEmailAddress("sashawalker@gmail.com");
        user2.setPhoneNumber("07467652710");
        user2.setRoles(Roles.DESIGNER);
        user2.setUsername("sashawalker");
        user2.setPassword("7dfe6320472n");

        userRepository.saveAll(List.of(user1, user2));

        designer1 = new Designer();
        designer1.setUser(user1);
        designer2 = new Designer();
        designer2.setUser(user2);

        designerRepository.saveAll(List.of(designer1, designer2));

        client1 = new Client();
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmailAddress("jessicacook@gmail.com");
        client1.setPhoneNumber("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer1);

        client2 = new Client();
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmailAddress("aprice@gmail.com");
        client2.setPhoneNumber("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");
        client2.setDesigner(designer1);

        client3 = new Client();
        client3.setFirstName("Simon");
        client3.setLastName("Harris");
        client3.setEmailAddress("harrissimon@gmail.com");
        client3.setPhoneNumber("07855443322");
        client3.setAddress("89 Riverbank Road, Birmingham, B23 O92");
        client3.setNotes("Loves minimalist design");
        client3.setDesigner(designer1);

        clientRepository.saveAll(List.of(client1, client2, client3));

    }

    /**
     * Tests if the Client can be found by their assigned designer's id
     */
    @Test
    @DisplayName("FindByDesigner: Finds client by Designer")
    public void testfindByDesigner_ReturnsClients() {

        // Arrange: Prepare pageable with page size
        Pageable pageable = PageRequest.of(0, 3);

        // Act: Query repository with designer's id
        Page<ClientSummaryDTO> result = clientRepository.findClientsByDesignerId(designer1.getId(), pageable);

        // Assert: Verify results match expected clients
        assertNotNull(result);
        assertEquals(result.getSize(), 3);
        assertEquals(result.getTotalPages(), 1);
        assertEquals(result.getContent().get(0).getFullName(), "Jessica Cook");

    }

    /**
     * Tests when the Client isnt found by Designer and returns a empty set
     */
    @Test
    @DisplayName("FindByDesigner: Clients not found by designer")
    public void testfindByDesignerReturnsEmptyList() {

        // Arrange: Mock Repository to test if the clients with designer2Id are
        // found

        Pageable pageable = PageRequest.of(0, 10);

        // Act: Query the repository with the designer2Id and pageable
        Page<ClientSummaryDTO> result = clientRepository.findClientsByDesignerId(designer2.getId(), pageable);

        // Assert: Verifies result's page is empty
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

}