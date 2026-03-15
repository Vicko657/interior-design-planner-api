package com.interiordesignplanner.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interiordesignplanner.client.Client;

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
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Project Repository Test Suite")
public class ProjectRepositoryTest {

    // Mock project repository
    @Mock
    private ProjectRepository projectRepository;

    private Deadline dtest1;
    private Deadline dtest2;
    private Deadline dtest3;
    private Project project2, project3;
    private Client client2;

    @BeforeEach
    public void setUp() {

        // Created mock project tests

        client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("Alex");
        client2.setLastName("Price");
        client2.setEmail("aprice@gmail.com");
        client2.setPhone("07828096962");
        client2.setAddress("249 The Grove, Reading, R84 J5N");
        client2.setNotes("Needs child-friendly furniture");

        dtest1 = new Deadline("Industrial Loft Redesign", ProjectStatus.ACTIVE, LocalDate.of(2026, 1, 25));
        dtest2 = new Deadline("Luxury Master Bedroom", ProjectStatus.PLANNING, LocalDate.of(2026, 5, 5));
        dtest3 = new Deadline("Scandinavian Living Room", ProjectStatus.ON_HOLD, LocalDate.of(2026, 1, 10));

        project2 = new Project();
        project2.setId(2L);
        project2.setClient(client2);
        project2.setProjectName("Luxury Master Bedroom");
        project2.setStatus(ProjectStatus.ACTIVE);
        project2.setBudget(BigDecimal.valueOf(5000.00));
        project2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
        project2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
        project2.setStartDate(LocalDate.of(2025, 11, 10));
        project2.setDueDate(LocalDate.of(2026, 5, 5));

        project3 = new Project();
        project3.setId(3L);
        project3.setClient(client2);
        project3.setProjectName("Industrial Loft Redesign");
        project3.setStatus(ProjectStatus.ACTIVE);
        project3.setBudget(BigDecimal.valueOf(20000.00));
        project3.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        project3.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project3.setStartDate(LocalDate.of(2025, 07, 20));
        project3.setDueDate(LocalDate.of(2026, 01, 25));

    }

    /**
     * Tests if the Project can be found by their status
     */
    @Test
    @DisplayName("GetProjectsByStatus: Finds project by status")
    public void testGetProjectsByStatus_ReturnsProjects() {

        // Arrange: Mock repository to return test for status (ACTIVE).

        when(projectRepository.findProjectsByStatus(ProjectStatus.ACTIVE)).thenReturn(List.of(project2, project3));

        // Act: Query the repository with status (ACTIVE)
        List<Project> result = projectRepository.findProjectsByStatus(ProjectStatus.ACTIVE);

        // Assert: Verify that the result only returns two projects and is (ACTIVE)
        assertNotNull(result);
        assertThat(2).isEqualTo(result.size());
        assertThat(project3).isEqualTo(result.get(1));
        verify(projectRepository).findProjectsByStatus(ProjectStatus.ACTIVE);

    }

    /**
     * Tests when the Project status is invalid and is not found
     */
    @Test
    @DisplayName("GetProjectsByStatus: Project is not found with status")
    public void testGetProjectByStatus_ReturnsEmptyList() {

        // Arrange: Mock repository to test a different status (COMPLETED).
        when(projectRepository.findProjectsByStatus(ProjectStatus.COMPLETED)).thenReturn(Collections.emptyList());

        // Act: Query the repository with status (COMPLETED)
        List<Project> result = projectRepository.findProjectsByStatus(ProjectStatus.COMPLETED);

        // Assert: Verify that the result doesnt return test
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(projectRepository).findProjectsByStatus(ProjectStatus.COMPLETED);

    }

    /**
     * Tests if projects are returned in order of due date
     */
    @Test
    @DisplayName("findAllProjectsDue: Finds all projects with due date")
    public void testfindAllProjectsDue_ReturnsProjects() {

        // Arrange: Mock repository to test if all the projects return in order of due
        // date
        when(projectRepository.getAllProjectsOrderByDueDate()).thenReturn(List.of(dtest3, dtest1, dtest2));

        // Act: Query the repository with the getAllProjectsOrderByDueDate method
        List<Deadline> result = projectRepository.getAllProjectsOrderByDueDate();

        // Assert: Verify that the results return in order
        assertNotNull(result);
        assertThat(result.get(0).dueDate()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(result.get(1).dueDate()).isEqualTo(LocalDate.of(2026, 1, 25));
        assertThat(result.get(2).dueDate()).isEqualTo(LocalDate.of(2026, 5, 5));
        verify(projectRepository).getAllProjectsOrderByDueDate();

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(projectRepository);
    }

}
