package com.interiordesignplanner.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;

/**
 * Unit tests for {@link ProjectRepository}.
 *
 * <p>
 * This class verifies the persistence and retrieval of {@link Project}
 * entities.
 * It focuses on repository-level behavior including:
 * <p>
 * The tests use mocked repository behavior.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName(value = "Project Repository Test Suite")
public class ProjectRepositoryTest {

    // Mock project repository
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    public ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerRepository designerRepository;

    private Project project2, project3;
    private Client client2;

    public Designer designer1, designer2;

    public User user1, user2;

    @BeforeEach
    public void setUp() {

        projectRepository.deleteAll();
        clientRepository.deleteAll();
        designerRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setFirstName("Dove");
        user1.setLastName("White");
        user1.setEmail("dovewhite@gmail.com");
        user1.setMobileNumber("07223180736");
        user1.setRoles(Roles.DESIGNER);
        user1.setUsername("dovewhite");
        user1.setPassword("gsjgtq893x");

        user2 = new User();
        user2.setFirstName("Sasha");
        user2.setLastName("Walker");
        user2.setEmail("sashawalker@gmail.com");
        user2.setMobileNumber("07467652710");
        user2.setRoles(Roles.DESIGNER);
        user2.setUsername("sashawalker");
        user2.setPassword("7dfe6320472n");

        userRepository.saveAll(List.of(user1, user2));

        designer1 = new Designer();
        designer1.setUser(user1);
        designer2 = new Designer();
        designer2.setUser(user2);

        designerRepository.saveAll(List.of(designer1, designer2));

        client2 = new Client();
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmail("aprice@gmail.com");
        client2.setPhone("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");
        client2.setDesigner(designer1);

        clientRepository.save(client2);

        project2 = new Project();
        project2.setClient(client2);
        project2.setProjectName("Luxury Master Bedroom");
        project2.setStatus(ProjectStatus.ACTIVE);
        project2.setBudget(BigDecimal.valueOf(5000.00));
        project2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
        project2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
        project2.setStartDate(LocalDate.of(2025, 11, 10));
        project2.setDueDate(LocalDate.of(2026, 5, 5));

        project3 = new Project();
        project3.setClient(client2);
        project3.setProjectName("Industrial Loft Redesign");
        project3.setStatus(ProjectStatus.ACTIVE);
        project3.setBudget(BigDecimal.valueOf(20000.00));
        project3.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        project3.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project3.setStartDate(LocalDate.of(2025, 07, 20));
        project3.setDueDate(LocalDate.of(2026, 01, 25));

        projectRepository.saveAll(List.of(project2, project3));

    }

    /**
     * Tests if the Project can be found by their status
     */
    @Test
    @DisplayName("GetProjectsByStatus: Finds project by status")
    public void testGetProjectsByStatus_ReturnsProjects() {

        // Arrange: Mock repository to return test for status (ACTIVE).
        Pageable pageable = PageRequest.of(0, 3);

        // Act: Query the repository with status (ACTIVE)
        Page<Project> result = projectRepository.findProjectsByStatus(ProjectStatus.ACTIVE, pageable);

        // Assert: Verify that the result only returns two projects and is (ACTIVE)
        assertNotNull(result);
        assertThat(2).isEqualTo(result.getTotalElements());
        assertThat(project3).isEqualTo(result.getContent().get(1));

    }

    /**
     * Tests when the Project status is invalid and is not found
     */
    @Test
    @DisplayName("GetProjectsByStatus: Project is not found with status")
    public void testGetProjectByStatus_ReturnsEmptyList() {

        // Arrange: Mock repository to test a different status (COMPLETED).
        Pageable pageable = PageRequest.of(0, 3);

        // Act: Query the repository with status (COMPLETED)
        Page<Project> result = projectRepository.findProjectsByStatus(ProjectStatus.COMPLETED, pageable);

        // Assert: Verify that the result doesnt return test
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    /**
     * Tests if projects are returned in order of due date
     */
    @Test
    @DisplayName("findAllProjectsDue: Finds all projects with due date")
    public void testfindAllProjectsDue_ReturnsProjects() {

        // Arrange: Mock repository to test if all the projects return in order of due
        // date
        Pageable pageable = PageRequest.of(0, 4);

        // Act: Query the repository with the getAllProjectsOrderByDueDate method
        Page<Deadline> result = projectRepository.getAllProjectsOrderByDueDate(pageable);

        // Assert: Verify that the results return in order
        assertNotNull(result);
        assertThat(result.getContent().get(0).dueDate()).isEqualTo(LocalDate.of(2026, 1, 25));
        assertThat(result.getContent().get(1).dueDate()).isEqualTo(LocalDate.of(2026, 5, 5));

    }

    /**
     * Tests if the Project can be found by their assigned client's, designer id
     */
    @Test
    @DisplayName("FindByDesigner: Finds project by Designer")
    public void testfindByDesigner_ReturnsProjects() {

        // Arrange: Prepare pageable with page size
        Pageable pageable = PageRequest.of(0, 3);

        // Act: Query repository with designer's id
        Page<ProjectSummaryDTO> result = projectRepository.findProjectsByDesignerId(designer1.getId(), pageable);

        // Assert: Verify results match expected clients
        assertNotNull(result);
        assertEquals(result.getSize(), 3);
        assertEquals(result.getTotalPages(), 1);
        assertEquals(result.getContent().get(0).getDescription(),
                "Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");

    }

    /**
     * Tests when the Project isnt found by Designer and returns a empty set
     */
    @Test
    @DisplayName("FindByDesigner: Projects not found by designer")
    public void testfindByDesignerReturnsEmptyList() {

        // Arrange: Mock Repository to test if the projects with designer2Id are
        // found

        Pageable pageable = PageRequest.of(0, 10);

        // Act: Query the repository with the designer2Id and pageable
        Page<ProjectSummaryDTO> result = projectRepository.findProjectsByDesignerId(designer2.getId(), pageable);

        // Assert: Verifies result's page is empty
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

}
