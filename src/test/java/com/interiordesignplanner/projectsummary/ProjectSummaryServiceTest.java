package com.interiordesignplanner.projectsummary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.inventory.Item;
import com.interiordesignplanner.mapper.ProjectMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectService;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomService;
import com.interiordesignplanner.room.RoomType;
import com.interiordesignplanner.task.Task;

/**
 * Unit tests for {@link ProjectSummaryService}.
 *
 * <p>
 * Verifies project summary business logic.
 * </p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Project Summary Service Test Suite")
public class ProjectSummaryServiceTest {

        @Mock
        private ProjectMapper projectMapper;

        @InjectMocks
        public ProjectSummaryService projectSummaryService;

        @Mock
        private ClientService clientService;

        @Mock
        private ProjectService projectService;

        @Mock
        private RoomService roomService;

        @Mock
        private PasswordEncoder passwordEncoder;

        private Client client;
        private Project project, project2;
        private User user;
        private Designer designer;
        private Item item, item2, item3;

        @BeforeEach
        // Created mock project tests
        public void setUp() {

                // Added Project Mapper to convert dtos and entities
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration()
                                .setMatchingStrategy(MatchingStrategies.STRICT);

                modelMapper.createTypeMap(Project.class, ProjectSummaryDTO.class)
                                .setPostConverter(convert -> {
                                        Project source = convert.getSource();
                                        ProjectSummaryDTO destination = convert.getDestination();

                                        if (source.getClient() != null) {
                                                destination.setClientId(source.getClient().getId());
                                                destination.setClientName(source.getClient().getFirstName() + " "
                                                                + source.getClient()
                                                                                .getLastName());
                                        }

                                        if (source.getRoom() != null) {
                                                destination.setHeight(source.getRoom().getHeight());
                                                destination.setLength(source.getRoom().getLength());
                                                destination.setWidth(source.getRoom().getWidth());
                                                destination.setUnit(source.getRoom().getUnit());
                                                destination.setRoomId(source.getRoom().getId());
                                                destination.setRoom(source.getRoom().getType());
                                        }
                                        return destination;
                                });

                this.projectMapper = new ProjectMapper(modelMapper);

                projectSummaryService = new ProjectSummaryService(clientService, projectMapper, roomService,
                                projectService);

                user = new User();
                user.setId(1L);
                user.setFirstName("Sam");
                user.setLastName("Williams");
                user.setEmailAddress("samwilliams@gmail.com");
                user.setPhoneNumber("07348294736");
                user.setRoles(Roles.DESIGNER);
                user.setUsername("sam");
                user.setPassword(passwordEncoder.encode("huwa71egyw"));

                designer = new Designer();
                designer.setId(1L);
                designer.setUser(user);

                // Created mock Client
                client = new Client();
                client.setId(1L);
                client.setFirstName("Jessica");
                client.setLastName("Cook");
                client.setEmailAddress("jessicacook@gmail.com");
                client.setPhoneNumber("07314708068");
                client.setAddress("33 Elm Street, London, N2R 652");
                client.setNotes("Prefers eco-friendly materials");

                // Created mock Projects
                project = new Project();
                project.setId(1L);
                project.setClient(client);
                project.setProjectName("Industrial Loft Redesign");
                project.setStatus(ProjectStatus.PLANNING);
                project.setBudget(BigDecimal.valueOf(40000.00));
                project.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
                project.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                project.setStartDate(LocalDate.of(2025, 07, 20));
                project.setDueDate(LocalDate.of(2026, 01, 25));

                // Created mock Projects
                project2 = new Project();
                project2.setId(2L);
                project2.setClient(client);
                project2.setProjectName("Luxury Master Bedroom");
                project2.setStatus(ProjectStatus.PLANNING);
                project2.setBudget(BigDecimal.valueOf(5000.00));
                project2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
                project2.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                project2.setStartDate(LocalDate.of(2025, 11, 10));
                project2.setDueDate(LocalDate.of(2026, 5, 5));
                project2.setRoom(null);

                List<Item> inventory = new ArrayList<>();
                List<Task> checkList = new ArrayList<>();

                Room room1 = new Room();
                room1.setId(1L);
                room1.setProject(project);
                room1.setType(RoomType.LOFT);
                room1.setHeight(10.4);
                room1.setLength(25.3);
                room1.setWidth(7.2);
                room1.setInventory(inventory);
                room1.setChecklist(checkList);
                room1.setUnit("m");
                room1.setProject(project);

                project.setRoom(room1);

                item = new Item();
                item.setImageUrl("/img/product1.png");
                item.setItemName("Coffee Table");
                item.setDescription(
                                "Finished in chalked solid mango wood the Imogen coffee table features an oval table top and chunky curved legs. The chalked mango wood finish adds texture and shows the natural wood grain for a rustic look.");
                item.setDimensions("H45cm W110cm D55cm");
                item.setOrdered(true);
                item.setPrice(BigDecimal.valueOf(119.99));
                item.setQuantity(1);

                item2 = new Item();
                item2.setImageUrl("/img/product2.png");
                item2.setItemName("Modern Minimalist Spanish Marble Copper Wall Sconce LED 1-Light");
                item2.setDescription(
                                "Designed with a sleek minimalist profile, it combines a resin marble-effect body with subtle copper-toned details for a refined, contemporary look. ");
                item2.setDimensions("40x6cm");
                item2.setOrdered(false);
                item2.setPrice(BigDecimal.valueOf(79.95));
                item2.setQuantity(5);
                item2.setLink(
                                "https://lassonliving.com/products/modern-minimalist-spanish-marble-copper-wall-sconce-led-1-light?currency=GBP&variant=52319038767450&utm_source=google&utm_medium=cpc&utm_campaign=Google%20Shopping&stkn=142a61490561&tw_source=google&tw_adid=&tw_campaign=23009353675&tw_kwdid=&gad_source=1&gad_campaignid=23009357257&gbraid=0AAAABBEvGio02gWJTA9FHptiW2zHPR6nK&gclid=CjwKCAjwjtTNBhB0EiwAuswYhtBMeo12PXoGWb6k-H7eZgOOV3jZ3PlZnxaMiBNW8DXZ8bySHsVKDhoCVe4QAvD_BwE");

                item3 = new Item();
                item3.setImageUrl("/img/product3.png");
                item3.setItemName("Barstools");
                item3.setDescription(
                                "Designed with a sumptuous velvet seat and a sturdy metal frame, this barstool offers both luxury and durability.");
                item3.setPrice(BigDecimal.valueOf(152.00));
                item3.setQuantity(2);
                item3.setDimensions("W: 47, D: 51, H: 88cm");
                item3.setLink(
                                "https://dusk.com/products/mollie-set-of-2-barstools-cappuccino?variant=55388585918842&gad_source=1&gad_campaignid=21757503987&gbraid=0AAAAADNOeOVm_QYZzEg2oFlbs2I2wuZmD&gclid=CjwKCAjwjtTNBhB0EiwAuswYhjSsFcuAfKf4TY-c07OEm4GAnFZXefbe5Uv5vgGlEPwFGe4lq3lmUxoCJbIQAvD_BwE");

                inventory.add(item);
                inventory.add(item2);
                inventory.add(item3);

        }

        /**
         * Tests if Get project summary returns project details
         */
        @Test
        @DisplayName("GetProjectSummary: Returns project summaries for designer")
        public void testGetProjectSummary_ReturnsProject() {
                // Arrange: Mock project, client and room to get project summary

                when(projectService.findProject(project.getId())).thenReturn(project);

                when(clientService.findClient(project.getClient().getId())).thenReturn(client);

                when(roomService.calculateTotalInventory(project.getRoom().getId()))
                                .thenReturn(BigDecimal.valueOf(823.74));

                // Act: Query the service layer to check project details are returned
                ProjectSummaryDTO result = projectSummaryService.getProjectSummary(user.getUsername(), project.getId());

                // Assert: Verifies that the result is not null and project summary is retrieved
                assertNotNull(result);
                assertThat(result.getId()).isEqualTo(1L);
                assertThat(result.getRemainingBudget()).isEqualTo(BigDecimal.valueOf(39176.26));
                assertThat(result.getDescription())
                                .isEqualTo("Exposed brick walls, metal fixtures, and reclaimed wood accents");
                assertThat(result.getHeight()).isEqualTo(10.4);

        }

        /**
         * Tests if Get project summary returns empty room details
         */
        @Test
        @DisplayName("GetProjectSummary: Returns Empty Room")
        public void testGetProjectSummary_EmptyRoom() {
                // Arrange: Mock project to test if it returns empty room details

                when(projectService.findProject(project2.getId())).thenReturn(project2);

                when(clientService.findClient(project2.getClient().getId())).thenReturn(client);

                // Act: Query the service layer if a empty room details is returned
                ProjectSummaryDTO result = projectSummaryService.getProjectSummary(user.getUsername(),
                                project2.getId());

                // Assert: Verifies that the room details are empty
                assertNotNull(result);
                assertEquals(result.getProjectName(), "Luxury Master Bedroom");
                assertEquals(result.getRoom(), null);
                assertEquals(result.getRoomId(), null);
                assertEquals(result.getHeight(), 0.0);
                assertEquals(result.getLength(), 0.0);
                assertEquals(result.getWidth(), 0.0);
                assertEquals(result.getUnit(), " ");
                assertEquals(result.getRemainingBudget(), BigDecimal.valueOf(5000.00));

        }

}
