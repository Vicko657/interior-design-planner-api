package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.mapper.ProjectMapper;

/**
 * Unit tests for {@link ProjectService}.
 *
 * <p>
 * Validates project lifecycle management including creation,
 * updates and status transitions. Ensures relationships to Projects
 * and rooms are correctly handled.
 * </p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Project Service Test Suite")
public class ProjectServiceTest {

    // Mock project repository
    @Mock
    public ProjectRepository projectRepository;

    // Project mapper
    private ProjectMapper projectMapper;

    // Mock project service
    @InjectMocks
    public ProjectService projectService;

    // Mock Project service
    @Mock
    private ClientService clientService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private DesignerService designerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Client client1;

    private Project project1, project2;

    private User user, admin;

    private Designer designer;

    @BeforeEach
    // Created mock project tests
    public void setUp() {

        // Added Project Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Project.class, ProjectDTO.class)
                .addMappings(mapper -> mapper.skip(ProjectDTO::setClientName)).setPostConverter(convert -> {
                    Project source = convert.getSource();
                    ProjectDTO destination = convert.getDestination();
                    if (source.getClient() != null) {
                        destination.setClientName(
                                source.getClient().getFirstName() + " " + source.getClient().getLastName());
                    }
                    if (source.getRoom() != null) {
                        destination.setRoom(source.getRoom().getType());
                    }
                    return destination;
                });

        this.projectMapper = new ProjectMapper(modelMapper);

        projectService = new ProjectService(projectRepository, clientService, authenticationService,
                designerService, projectMapper);

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

        // Created mock Client
        client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmailAddress("jessicacook@gmail.com");
        client1.setPhoneNumber("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer);

        // Created mock Projects
        project1 = new Project();
        project1.setId(1L);
        project1.setClient(client1);
        project1.setProjectName("Industrial Loft Redesign");
        project1.setStatus(ProjectStatus.PLANNING);
        project1.setBudget(BigDecimal.valueOf(20000.00));
        project1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        project1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project1.setStartDate(LocalDate.of(2025, 07, 20));
        project1.setDueDate(LocalDate.of(2026, 01, 25));

        project2 = new Project();
        project2.setId(2L);
        project2.setClient(client1);
        project2.setProjectName("Luxury Master Bedroom");
        project2.setStatus(ProjectStatus.ON_HOLD);
        project2.setBudget(BigDecimal.valueOf(5000.00));
        project2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
        project2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
        project2.setStartDate(LocalDate.of(2025, 11, 10));
        project2.setDueDate(LocalDate.of(2026, 5, 5));

    }

    /**
     * Tests for checking if Get all projects returns a list of projects
     */
    @Test
    @DisplayName("GetAllProjects: Returns all of the projects in the database")
    public void testGetAllProjects_ReturnsAllProjects() {
        // Arrange: A list created with projects and mock Repository to test if all
        // projects are returned

        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);

        Page<Project> mockPage = new PageImpl<>(projects);

        when(projectRepository.findAll(pageable)).thenReturn(mockPage);

        // Act: Query the service layer the if all projects are returnes
        Page<ProjectDTO> result = projectService.getAllProjects(null, pageable);

        // Assert: Verifies that the result is not null and projects are retrieved
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);
        assertThat(result).extracting(ProjectDTO::getBudget).containsExactly(BigDecimal.valueOf(20000.00),
                BigDecimal.valueOf(5000.00));
        verify(projectRepository).findAll(any(
                Pageable.class));
        verifyNoMoreInteractions(projectRepository);

    }

    /**
     * Tests for checking if Get all projects returns one of project
     */
    @Test
    @DisplayName("GetAllProjects: Returns one project in the database")
    public void testGetAllCProjects_ReturnsFilteredProject() {
        // Arrange: A page created with projects, pageable and mock Repository to test
        // if
        // all
        // projects are returned

        String filter = "filter=room===Loft";

        Pageable pageable = PageRequest.of(2, 2);
        List<Project> projects = new ArrayList<>();
        projects.add(project1);

        Page<Project> mockPage = new PageImpl<>(projects);

        when(projectRepository.findAll(ArgumentMatchers.<Specification<Project>>any(), any(
                Pageable.class)))
                .thenReturn(mockPage);

        // Act: Query the service layer the if all projects are returned
        Page<ProjectDTO> result = projectService.getAllProjects(filter, pageable);

        // Assert: Verifies that the result is not null and projects are retrieved
        assertNotNull(result);
        assertEquals(result.getContent().size(), 1);
        assertThat(result).extracting(ProjectDTO::getId).containsExactly(1L);
        assertThat(result).extracting(ProjectDTO::getProjectName).containsExactly("Industrial Loft Redesign");
        verify(projectRepository).findAll(ArgumentMatchers.<Specification<Project>>any(), any(
                Pageable.class));
        verifyNoMoreInteractions(projectRepository);

    }

    /**
     * Tests for checking if Get all projects returns a empty list
     */
    @Test
    @DisplayName("GetAllProjects: Returns empty list")
    public void testGetAllProjects_ReturnsEmptyList() {
        // Arrange: Empty list is created and Mock Repository to test if it returns a
        // empty list

        String filter = "filter=notes==null";
        Pageable pageable = PageRequest.of(2, 2);
        Page<Project> projects = Page.empty();
        when(projectRepository.findAll(ArgumentMatchers.<Specification<Project>>any(), any(
                Pageable.class))).thenReturn(projects);

        Page<ProjectDTO> result = projectService.getAllProjects(filter, pageable);

        // Assert: Verifies that the result empty
        assertThat(result).isEqualTo(projects);
    }

    /**
     * Tests for checking if Get projects returns a page of projects for the
     * designer
     */
    @Test
    @DisplayName("GetProjectsByDesigner: Returns all of the projects for designer")
    public void testGetProjectsByDesigner_ReturnsAllProjects() {
        // Arrange: A page created with projects, pageable and mock Repository to test
        // if
        // all the designer's projects are returned

        ProjectSummaryDTO projectSummaryDTO1 = new ProjectSummaryDTO(1L, "Jessica Cook", "Industrial Loft Redesign",
                ProjectStatus.PLANNING, BigDecimal.valueOf(20000.00), LocalDate.of(2025, 07, 20),
                LocalDate.of(2026, 01, 25), "Exposed brick walls, metal fixtures, and reclaimed wood accents");

        ProjectSummaryDTO projectSummaryDTO2 = new ProjectSummaryDTO(2L, "Alex Price",
                "Luxury Master Bedroom",
                ProjectStatus.ON_HOLD, BigDecimal.valueOf(5000.00),
                LocalDate.of(2025, 11,
                        10),
                LocalDate.of(2026, 5, 5),
                "Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProjectSummaryDTO> mockPage = new PageImpl<>(List.of(projectSummaryDTO1, projectSummaryDTO2));

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(projectRepository.findProjectsByDesignerId(user.getId(), pageable))
                .thenReturn(mockPage);

        // Act: Query the service layer the if all the designer's clients are returned
        Page<ProjectSummaryDTO> result = projectService.getProjectsByDesigner(user.getUsername(), pageable);

        // Assert: Verifies that the result is not null and clients are retrieved
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);
        assertThat(result).extracting(ProjectSummaryDTO::getId).containsExactly(1L, 2L);
        assertThat(result).extracting(ProjectSummaryDTO::getClientName).containsExactly(
                "Jessica Cook", "Alex Price");
        verify(projectRepository).findProjectsByDesignerId(any(), any(
                Pageable.class));
        verifyNoMoreInteractions(projectRepository);

    }

    /**
     * Tests for checking if Get projects returns a page of projects for the
     * designer
     */
    @Test
    @DisplayName("GetProjectsByDesigner: Returns Empty Page")
    public void testGetProjectsByDesigner_EmptyPage() {
        // Arrange: Empty page is created and Mock Repository to test if it returns a
        // empty page

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProjectSummaryDTO> mockPage = Page.empty();

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(projectRepository.findProjectsByDesignerId(user.getId(), pageable))
                .thenReturn(mockPage);

        // Act: Query the service layer if a empty page is returned
        Page<ProjectSummaryDTO> result = projectService.getProjectsByDesigner(user.getUsername(), pageable);

        // Assert: Verifies that the page is empty
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 0);
        verify(projectRepository).findProjectsByDesignerId(any(), any(
                Pageable.class));
        verifyNoMoreInteractions(projectRepository);

    }

    /**
     * Tests for when the project is found with the project id
     */
    @Test
    @DisplayName("GetProject: Returns project by ID")
    public void testGetProject_ReturnsProject() {
        // Arrange: Sets the projectId and mocks the repository
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));

        // Act: Query the service layer to return the project with the id
        ProjectDTO result = projectService.getProjectById(projectId);

        // Assert: Verifies that the result is not null and a project with the same Id
        // is
        // returned
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(projectId);
        assertThat(result.getProjectName()).isEqualTo("Industrial Loft Redesign");
    }

    /**
     * Tests for when the Project is not found, returns a empty set and throws a
     * ProjectNotFoundException
     */
    @Test
    @DisplayName("GetProject: Project ID is not found")
    public void testGetProject_ReturnsNotFound() {
        // Arrange: Set the ProjectId and mock the repository
        Long ProjectId = 3L;
        String errorMessage = "Project is not found with " + "projectId" + ": " + ProjectId;

        when(projectRepository.findById(ProjectId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectById(ProjectId);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    /**
     * Tests for creating a new Project successfully
     */
    @Test
    @DisplayName("CreateProject: Adds a new Project")
    public void testCreateProject_ReturnsCreated() {

        // Arrange: Mock Repository to test if a new Project has been created
        ProjectCreateDTO projectDTO = new ProjectCreateDTO(client1, "Scandinavian Living Room",
                ProjectStatus.PLANNING,
                BigDecimal.valueOf(
                        140000.00),
                LocalDate.of(2025, 07,
                        20),
                LocalDate.of(2026, 01, 25), "Exposed brick walls, metal fixtures, and reclaimed wood accents",
                "https://meet.google.com/hyd-ken-csa");

        Project savedProject = new Project();
        savedProject.setId(40L);
        savedProject.setClient(client1);
        savedProject.setProjectName("Scandinavian Living Room");
        savedProject.setStatus(ProjectStatus.PLANNING);
        savedProject.setBudget(BigDecimal.valueOf(140000.00));
        savedProject.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        savedProject.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        savedProject.setStartDate(LocalDate.of(2025, 07, 20));
        savedProject.setDueDate(LocalDate.of(2026, 01, 25));

        when(clientService.findClient(1L)).thenReturn(client1);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // Act: Query the service layer the if Project is there
        ProjectDTO result = projectService.createProject(projectDTO, client1.getId(), user.getUsername());

        // Assert: Verifies that the result is not null and Project has been created
        assertNotNull(result);
        assertThat(result).extracting(ProjectDTO::getBudget).isEqualTo(BigDecimal.valueOf(140000.00));
        verify(projectRepository, times(1)).save(any(Project.class));

    }

    /**
     * Tests for updating a Project
     */
    @Test
    @DisplayName("UpdateProject: Updates Project details")
    public void testUpdateProject_ReturnsUpdated() {
        // Arrange: Sets the ProjectId and mocks the repository
        Long projectId = 2L;

        // Updated Telephone number
        ProjectUpdateDTO updatedProject = new ProjectUpdateDTO();
        updatedProject.setStatus(ProjectStatus.ACTIVE);

        when(clientService.findClient(1L)).thenReturn(client1);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project2));
        when(projectRepository.save(project2)).thenReturn(project2);

        // Act: Query the service layer to return the Project with the id and update the
        // Project's details
        ProjectDTO result = projectService.updateProject(projectId, updatedProject, user.getUsername());

        // Assert: Verifies that the Project was updated
        assertNotNull(result);
        assertEquals(result.getStatus(), ProjectStatus.ACTIVE);
        verify(projectRepository).findById(projectId);

    }

    /**
     * Tests for updating a Project and the Project is not found
     */
    @Test
    @DisplayName("UpdateProject: Project ID is not found")
    public void testUpdateProject_ReturnsNotFound() {
        // Arrange: Sets the ProjectId and mocks the repository

        Long projectId = 2L;
        String errorMessage = "Project is not found with " + "projectId" + ": " + projectId;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ProjectUpdateDTO updateProject = new ProjectUpdateDTO();
        updateProject.setBudget(BigDecimal.valueOf(40000.00));

        // Act: Queries if the exception is thrown if Project is not found when updating
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.updateProject(projectId, updateProject, user.getUsername());
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(projectRepository).findById(projectId);

    }

    /**
     * Tests for deleting a Project
     */
    @Test
    @DisplayName("DeleteProject: Deletes Project details")
    public void testDeleteProject_ReturnsDeleted() {
        // Arrange: Sets the ProjectId and mocks the repository

        when(clientService.findClient(1L)).thenReturn(client1);
        Long projectId = 2L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project2));

        // Act: Query the service layer to return the Project with the id and delete the
        // Project
        projectService.deleteProject(projectId, user.getUsername());

        // Assert: Verifies that the Project was deleted and is not found
        verify(projectRepository).delete(project2);
        verify(projectRepository).findById(projectId);

    }

    /**
     * Tests for deleting a Project and the Project is not found
     */
    @Test
    @DisplayName("DeleteProject: Project ID is not found")
    public void testDeleteProject_ReturnsNotFound() {
        // Arrange: Sets the ProjectId and mocks the repository

        Long projectId = 2L;
        String errorMessage = "Project is not found with " + "projectId" + ": " + projectId;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown if Project is not found when deleting
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.deleteProject(projectId, user.getUsername());
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).delete(any(Project.class));

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(projectRepository);
    }

}
