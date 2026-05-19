package com.interiordesignplanner.project;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName(value = "Project Controller Test Suite")
public class ProjectControllerTest {

        @Autowired
        private MockMvc mockMvc;

        // Converts the ProjectDTO into JSON
        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ProjectRepository projectRepository;

        @Autowired
        private ClientRepository clientRepository;

        private ProjectUpdateDTO projectUpdateDTO;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private DesignerRepository designerRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private Client client1;

        private Designer designer;

        @BeforeEach
        void setUp() {

                projectRepository.deleteAll();
                clientRepository.deleteAll();
                designerRepository.deleteAll();
                userRepository.deleteAll();

                User user = new User();
                user.setFirstName("Sam");
                user.setLastName("Williams");
                user.setEmailAddress("samwilliams@gmail.com");
                user.setPhoneNumber("07348294736");
                user.setRoles(Roles.DESIGNER);
                user.setUsername("sam");
                user.setPassword(passwordEncoder.encode("huwa71egyw"));
                userRepository.save(user);

                User admin = new User();
                admin.setFirstName("Grace");
                admin.setLastName("Smith");
                admin.setEmailAddress("gracesmith@gmail.com");
                admin.setPhoneNumber("07392648274");
                admin.setRoles(Roles.ADMIN);
                admin.setUsername("grace");
                admin.setPassword(passwordEncoder.encode("bchqwbbbqyw3"));
                userRepository.save(admin);

                designer = new Designer();
                designer.setUser(user);
                designerRepository.save(designer);

                client1 = new Client();
                client1.setFirstName("Jessica");
                client1.setLastName("Cook");
                client1.setEmailAddress("jessicacook@gmail.com");
                client1.setPhoneNumber("07314708068");
                client1.setAddress("33 Elm Street, London, N2R 652");
                client1.setNotes("Prefers eco-friendly materials");
                client1.setDesigner(designer);
                clientRepository.save(client1);

                Project project1 = new Project();
                project1.setClient(client1);
                project1.setProjectName("Industrial Loft Redesign");
                project1.setStatus(ProjectStatus.PLANNING);
                project1.setBudget(BigDecimal.valueOf(20000.00));
                project1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
                project1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                project1.setStartDate(LocalDate.of(2025, 07, 20));
                project1.setDueDate(LocalDate.of(2026, 04, 25));

                Project project2 = new Project();
                project2.setClient(client1);
                project2.setProjectName("Luxury Master Bedroom");
                project2.setStatus(ProjectStatus.ACTIVE);
                project2.setBudget(BigDecimal.valueOf(50000.00));
                project2.setDescription(
                                "Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
                project2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
                project2.setStartDate(LocalDate.of(2025, 11, 10));
                project2.setDueDate(LocalDate.of(2026, 5, 5));

                projectRepository.save(project1);
                projectRepository.save(project2);

                ProjectDTO projectDTO1 = new ProjectDTO();
                projectDTO1.setClientName(client1.getFirstName() + " " + client1.getLastName());
                projectDTO1.setProjectName("Industrial Loft Redesign");
                projectDTO1.setStatus(ProjectStatus.PLANNING);
                projectDTO1.setBudget(BigDecimal.valueOf(20000.00));
                projectDTO1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
                projectDTO1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                projectDTO1.setStartDate(LocalDate.of(2025, 07, 20));
                projectDTO1.setDueDate(LocalDate.of(2026, 04, 25));

                ProjectDTO projectDTO2 = new ProjectDTO();
                projectDTO2.setClientName(client1.getFirstName() + " " + client1.getLastName());
                projectDTO2.setProjectName("Luxury Master Bedroom");
                projectDTO2.setStatus(ProjectStatus.ACTIVE);
                projectDTO2.setBudget(BigDecimal.valueOf(20000.00));
                projectDTO2.setDescription(
                                "Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
                projectDTO2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
                projectDTO2.setStartDate(LocalDate.of(2025, 11, 10));
                projectDTO2.setDueDate(LocalDate.of(2026, 5, 5));

        }

        @Test
        @DisplayName("GetAllProjects: Should return all Projects")
        @WithMockUser(roles = "ADMIN")
        void testGetAllProjects() throws Exception {

                mockMvc.perform(get("/api/admin/projects?page=0&size=10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(2)))
                                .andExpect(jsonPath("$.content[0].budget", is(
                                                20000.00)))
                                .andExpect(jsonPath("$.content[1].projectName", is("Luxury Master Bedroom")));

        }

        @Test
        @DisplayName("GetAllProjects: Should return one Project")
        @WithMockUser(roles = { "ADMIN" })
        void testGetAllProjects_ReturnOneProject() throws Exception {

                mockMvc.perform(get("/api/admin/projects?filter=status==ACTIVE")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(1)))
                                .andExpect(jsonPath("$.content[0].budget", is(
                                                50000.00)))
                                .andExpect(jsonPath("$.content[0].projectName", is("Luxury Master Bedroom")));

        }

        @Test
        @DisplayName("GetProjects: Should return all Projects")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testGetClients() throws Exception {

                mockMvc.perform(get("/api/projects")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(2)))
                                .andExpect(jsonPath("$.content[0].clientName").value("Jessica Cook"))
                                .andExpect(jsonPath("$.content[1].dueDate", is("2026-05-05")));

        }

        @Test
        @DisplayName("GetProjectById: Should return a Project")
        @WithMockUser(roles = "ADMIN")
        void testGetProjectById() throws Exception {

                mockMvc.perform(get("/api/admin/projects/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.status", is(
                                                "PLANNING")))
                                .andExpect(jsonPath("$.description",
                                                is("Exposed brick walls, metal fixtures, and reclaimed wood accents")));

        }

        @Test
        @DisplayName("GetProjectById: Project Not Found")
        @WithMockUser(roles = "ADMIN")
        void testGetProjectById_NotFound() throws Exception {

                mockMvc.perform(get("/api/admin/projects/33")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Project is not found with projectId: 33")));

        }

        @Test
        @DisplayName("CreateProject: Project is created")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testCreateProject() throws Exception {
                // Given

                ProjectCreateDTO project3 = new ProjectCreateDTO();
                project3.setProjectName("Scandinavian Living Room");
                project3.setBudget(BigDecimal.valueOf(8000.00));
                project3.setStatus(ProjectStatus.PLANNING);
                project3.setDescription("Light lighting and open space");
                project3.setMeetingURL("https://meet.google.com/7tf-do9-34s");
                project3.setStartDate(LocalDate.of(2025, 07, 20));
                project3.setDueDate(LocalDate.of(2026, 10, 25));
                project3.setClient(client1);

                // When/Then
                mockMvc.perform(post("/api/projects/{clientId}", client1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(project3)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(3)));

        }

        @Test
        @DisplayName("CreateProject: Missing ProjectName")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testCreateProject_ValidationFailure() throws Exception {
                // Given
                ProjectCreateDTO project2 = new ProjectCreateDTO();
                project2.setStatus(ProjectStatus.ACTIVE);
                project2.setBudget(BigDecimal.valueOf(20000.00));
                project2.setDescription(
                                "Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
                project2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
                project2.setStartDate(LocalDate.of(2025, 11, 10));
                project2.setDueDate(LocalDate.of(2026, 5, 5));

                // When/Then
                mockMvc.perform(post("/api/projects/{clientId}", client1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(project2)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.projectName", is("Project name is required")));

        }

        @Test
        @DisplayName("UpdateProject: Project's Due Date is updated")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testUpdateProject() throws Exception {
                // Given
                projectUpdateDTO = new ProjectUpdateDTO();
                projectUpdateDTO.setDueDate(LocalDate.of(2026, 8, 19));

                // When/Then
                mockMvc.perform(put("/api/projects/{id}", 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectUpdateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.dueDate", is("2026-08-19")));
        }

        @Test
        @DisplayName("UpdateProject: Project not found")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testUpdateProject_NotFound() throws Exception {

                projectUpdateDTO = new ProjectUpdateDTO();
                projectUpdateDTO.setStatus(ProjectStatus.COMPLETED);

                mockMvc.perform(put("/api/projects/9")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectUpdateDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Project is not found with projectId: 9")));

        }

        @Test
        @DisplayName("DeleteProject: Project is deleted")
        @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testDeleteProject() throws Exception {

                // When/Then
                mockMvc.perform(delete("/api/projects/2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

        }

}
