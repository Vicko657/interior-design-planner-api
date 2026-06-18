package com.interiordesignplanner.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.dashboard.DashboardDTO.CurrentBudget;
import com.interiordesignplanner.dashboard.DashboardDTO.ProjectProgress;
import com.interiordesignplanner.dashboard.DashboardDTO.RecentTasks;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.project.ProjectRepository;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.RoomRepository;

/**
 * Unit tests for {@link DashboardService}.
 *
 * <p>
 * Verifies dashboard business logic.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Dashboard Service Test Suite")
public class DashboardServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private DesignerService designerService;

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private Designer designer;
    private Client client1;
    private List<ProjectProgress> projectProgress;
    private List<CurrentBudget> currentBudget;
    private List<RecentTasks> recentTasks;
    private ProjectProgress project1, project2;
    private CurrentBudget budget;
    private RecentTasks task1, task2, task3, task4;
    private List<Object[]> totalActiveProjects, totalCompletedProjects, totalProjects;
    private Integer active, completed, total;
    private Pageable pageable1, pageable2;

    @BeforeEach
    public void setUp() {

        dashboardService = new DashboardService(projectRepository, authenticationService, designerService,
                roomRepository);

        user = new User();
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmailAddress("samwilliams@gmail.com");
        user.setPhoneNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("sam");
        user.setPassword(passwordEncoder.encode("huwa71egyw"));

        User admin = new User();
        admin.setFirstName("Grace");
        admin.setLastName("Smith");
        admin.setEmailAddress("gracesmith@gmail.com");
        admin.setPhoneNumber("07392648274");
        admin.setRoles(Roles.ADMIN);
        admin.setUsername("grace");
        admin.setPassword(passwordEncoder.encode("bchqwbbbqyw3"));

        designer = new Designer();
        designer.setUser(user);

        client1 = new Client();
        client1.setFirstName("John");
        client1.setLastName("Moss");
        client1.setEmailAddress("johnmoss@gmail.com");
        client1.setPhoneNumber("07894832061");
        client1.setAddress("4B Avenue, Reading, R6 6E3");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer);

        recentTasks = new ArrayList<>();
        projectProgress = new ArrayList<>();
        currentBudget = new ArrayList<>();

        project1 = new ProjectProgress("Modern Living Room", ProjectStatus.ACTIVE, LocalDate.of(2026, 9, 25));
        project2 = new ProjectProgress("Scandinavian Loft", ProjectStatus.PLANNING, LocalDate.of(2026, 10, 25));

        budget = new CurrentBudget(new BigDecimal(25000.00), "John Moss");

        task1 = new RecentTasks("Flooring", true, "Modern Living Room");
        task2 = new RecentTasks("Order Tiles", false, "Modern Living Room");
        task3 = new RecentTasks("Contact Contractors", true, "Scandinavian Loft");
        task4 = new RecentTasks("Order Curtains", false, "Scandinavian Loft");

        pageable1 = PageRequest.of(0, 3);

        pageable2 = PageRequest.of(0, 4);

        projectProgress.add(project1);
        projectProgress.add(project2);
        currentBudget.add(budget);
        recentTasks.add(task1);
        recentTasks.add(task2);
        recentTasks.add(task3);
        recentTasks.add(task4);

        totalActiveProjects = new ArrayList<>();
        totalCompletedProjects = new ArrayList<>();
        totalProjects = new ArrayList<>();

        totalActiveProjects.add(0, new Object[] { project1 });

        totalProjects.add(0, new Object[] { project1 });
        totalProjects.add(1, new Object[] { project2 });

        active = totalActiveProjects.size();
        completed = totalCompletedProjects.size();
        total = totalProjects.size();

    }

    /**
     * Tests if Get dashboard returns designer's dashboard
     */
    @Test
    @DisplayName("GetDashboard: Returns authenticated designer's dashboard")
    public void testGetDashboard_ReturnsDashboard() {

        // Arrange: mocks the repositorys and mocks designer's dashboard

        when(authenticationService.findUser(user.getUsername())).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(projectRepository.findClientTotalBudget(user.getId(), pageable2)).thenReturn(currentBudget);

        when(projectRepository.findProjectProgress(user.getId(), pageable1)).thenReturn(projectProgress);

        when(roomRepository.findRecentTasks(user.getId(), pageable2)).thenReturn(recentTasks);

        when(projectRepository.findTotalProjectStatus(user.getId(), ProjectStatus.ACTIVE))
                .thenReturn(totalActiveProjects);

        when(projectRepository.findTotalProjects(user.getId())).thenReturn(totalProjects);

        when(projectRepository.findTotalProjectStatus(user.getId(), ProjectStatus.COMPLETED))
                .thenReturn(totalCompletedProjects);

        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setName(user.getFirstName() + " " + user.getLastName());
        dashboardDTO.setProjectProgress(projectProgress);
        dashboardDTO.setCompletedProjects(completed);
        dashboardDTO.setTotalProjects(total);
        dashboardDTO.setActiveProjects(active);
        dashboardDTO.setRecentTasks(recentTasks);
        dashboardDTO.setBudget(currentBudget);

        // Act: Query the service layer to check the designer is authenticated and
        // return
        // their dashboard
        DashboardDTO result = dashboardService.getDashboard(user.getUsername());

        // Assert: Verifies that the result is not null and dashboard is retrieved
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Sam Williams");
        assertThat(result.getProjectProgress().get(0).status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(result.getRecentTasks().size()).isEqualTo(4);
        assertThat(result.getRecentTasks().get(2).isCompleted()).isEqualTo(true);
        assertThat(result.getBudget().get(0).budget()).isEqualTo(new BigDecimal(25000.00));
        assertThat(result.getCompletedProjects()).isEqualTo(0);
        assertThat(result.getActiveProjects()).isEqualTo(1);
        assertThat(result.getTotalProjects()).isEqualTo(2);
    }

    /**
     * Tests if Get dashboard returns empty results
     */
    @Test
    @DisplayName("GetDashboard: Returns Empty Results")
    public void testGetDashboard_ReturnsEmptyResults() {

        // Arrange: mocks the repositorys and mocks designer's dashboard

        when(authenticationService.findUser(user.getUsername())).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(projectRepository.findClientTotalBudget(user.getId(), pageable2)).thenReturn(List.of());

        when(projectRepository.findProjectProgress(user.getId(), pageable1)).thenReturn(List.of());

        when(roomRepository.findRecentTasks(user.getId(), pageable2)).thenReturn(List.of());

        when(projectRepository.findTotalProjectStatus(user.getId(), ProjectStatus.ACTIVE))
                .thenReturn(List.of());

        when(projectRepository.findTotalProjects(user.getId())).thenReturn(List.of());

        when(projectRepository.findTotalProjectStatus(user.getId(), ProjectStatus.COMPLETED))
                .thenReturn(List.of());

        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setName(user.getFirstName() + " " + user.getLastName());
        dashboardDTO.setProjectProgress(List.of());
        dashboardDTO.setCompletedProjects(0);
        dashboardDTO.setTotalProjects(0);
        dashboardDTO.setActiveProjects(0);
        dashboardDTO.setRecentTasks(List.of());
        dashboardDTO.setBudget(List.of());

        // Act: Query the service layer to check the designer is authenticated and
        // return
        // their dashboard
        DashboardDTO result = dashboardService.getDashboard(user.getUsername());

        // Assert: Verifies that the result is not null and dashboard is retrieved with
        // empty results
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Sam Williams");
        assertThat(result.getProjectProgress().size()).isEqualTo(0);
        assertThat(result.getRecentTasks().size()).isEqualTo(0);
        assertThat(result.getBudget().size()).isEqualTo(0);
        assertThat(result.getCompletedProjects()).isEqualTo(0);
        assertThat(result.getActiveProjects()).isEqualTo(0);
        assertThat(result.getTotalProjects()).isEqualTo(0);
    }

}
