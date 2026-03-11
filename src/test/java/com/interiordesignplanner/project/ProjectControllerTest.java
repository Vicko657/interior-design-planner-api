package com.interiordesignplanner.project;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.exceptions.ProjectNotFoundException;

@WebMvcTest(ProjectController.class)
@DisplayName(value = "Project Controller Test Suite")
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Converts the ProjectDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Mockito Bean replaces MockBean(depreciated)
    @MockitoBean
    private ProjectService projectService;

    private ProjectDTO projectDTO1, projectDTO2;

    private ProjectCreateDTO projectCreateDTO, projectCreateDTO2;

    private ProjectUpdateDTO projectUpdateDTO;

    private Client client1;

    @BeforeEach
    void setUp() {

        client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Jessica");
        client1.setLastName("Cook");
        client1.setEmail("jessicacook@gmail.com");
        client1.setPhone("07314708068");
        client1.setAddress("33 Elm Street, London, N2R 652");
        client1.setNotes("Prefers eco-friendly materials");

        projectCreateDTO = new ProjectCreateDTO();
        projectCreateDTO.setProjectName("Industrial Loft Redesign");
        projectCreateDTO.setBudget(20000);
        projectCreateDTO.setStatus(ProjectStatus.PLANNING);
        projectCreateDTO.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        projectCreateDTO.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        projectCreateDTO.setStartDate(LocalDate.of(2025, 07, 20));
        projectCreateDTO.setDueDate(LocalDate.of(2026, 01, 25));

        projectDTO1 = new ProjectDTO();
        projectDTO1.setId(1L);
        projectDTO1.setClientName(client1.getFirstName() + " " + client1.getLastName());
        projectDTO1.setProjectName("Industrial Loft Redesign");
        projectDTO1.setStatus(ProjectStatus.PLANNING);
        projectDTO1.setBudget(20000);
        projectDTO1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        projectDTO1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        projectDTO1.setStartDate(LocalDate.of(2025, 07, 20));
        projectDTO1.setDueDate(LocalDate.of(2026, 01, 25));

        projectDTO2 = new ProjectDTO();
        projectDTO2.setId(2L);
        projectDTO2.setClientName(client1.getFirstName() + " " + client1.getLastName());
        projectDTO2.setProjectName("Luxury Master Bedroom");
        projectDTO2.setStatus(ProjectStatus.ACTIVE);
        projectDTO2.setBudget(20000);
        projectDTO2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
        projectDTO2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
        projectDTO2.setStartDate(LocalDate.of(2025, 11, 10));
        projectDTO2.setDueDate(LocalDate.of(2026, 5, 5));

    }

    @Test
    @DisplayName("GetAllProjects: Should return all Projects")
    void testGetAllProjects() throws Exception {

        // Given
        when(projectService.getAllProjects()).thenReturn(List.of(projectDTO1, projectDTO2));

        // When/Then
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].budget", is(
                        20000)))
                .andExpect(jsonPath("$[1].projectName", is("Luxury Master Bedroom")));

        verify(projectService).getAllProjects();
    }

    @Test
    @DisplayName("GetProjectById: Should return a Project")
    void testGetProjectById() throws Exception {
        // Given
        when(projectService.getProjectById(1L)).thenReturn(projectDTO1);

        // When/Then
        mockMvc.perform(get("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(
                        "PLANNING")))
                .andExpect(jsonPath("$.description",
                        is("Exposed brick walls, metal fixtures, and reclaimed wood accents")));

        verify(projectService).getProjectById(1L);
    }

    @Test
    @DisplayName("GetProjectById: Project Not Found")
    void testGetProjectById_NotFound() throws Exception {
        // Given
        when(projectService.getProjectById(33L)).thenThrow(new ProjectNotFoundException("projectId", 33L));

        // When/Then
        mockMvc.perform(get("/api/projects/33")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project is not found with projectId: 33")));

        verify(projectService).getProjectById(33L);
    }

    @Test
    @DisplayName("CreateProject: Project is created")
    void testCreateProject() throws Exception {
        // Given

        Long clientId = client1.getId();
        when(projectService.createProject(projectCreateDTO, clientId)).thenReturn(projectDTO1);

        // When/Then
        mockMvc.perform(post("/api/projects/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(projectService).createProject(projectCreateDTO, clientId);
    }

    @Test
    @DisplayName("CreateProject: Missing ProjectName")
    void testCreateProject_ValidationFailure() throws Exception {
        // Given
        projectCreateDTO2 = new ProjectCreateDTO();
        projectCreateDTO2.setStatus(ProjectStatus.ACTIVE);
        projectCreateDTO2.setBudget(20000);
        projectCreateDTO2.setDescription("Custom wardrobes, soft lighting, and premium fabrics for a hotel-like feel.");
        projectCreateDTO2.setMeetingURL("https://meet.google.com/lhv-erf-oub");
        projectCreateDTO2.setStartDate(LocalDate.of(2025, 11, 10));
        projectCreateDTO2.setDueDate(LocalDate.of(2026, 5, 5));

        // When/Then
        mockMvc.perform(post("/api/projects/{clientId}", client1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectCreateDTO2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("Project name is required")));

        verify(projectService, never()).createProject(projectCreateDTO, client1.getId());
    }

    @Test
    @DisplayName("UpdateProject: Project's Due Date is updated")
    void testUpdateProject() throws Exception {
        // Given
        Long id = projectDTO2.getId();
        projectUpdateDTO = new ProjectUpdateDTO();
        projectUpdateDTO.setDueDate(LocalDate.of(2026, 8, 19));

        projectDTO2.setDueDate(LocalDate.of(2026, 8, 19));

        when(projectService.updateProject(id, projectUpdateDTO)).thenReturn(projectDTO2);

        // When/Then
        mockMvc.perform(put("/api/projects/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dueDate", is("2026-08-19")));

        verify(projectService).updateProject(id, projectUpdateDTO);
    }

    @Test
    @DisplayName("UpdateProject: Project not found")
    void testUpdateProject_NotFound() throws Exception {
        // Given
        Long id = 9L;
        projectUpdateDTO = new ProjectUpdateDTO();
        when(projectService.updateProject(id, projectUpdateDTO))
                .thenThrow(new ProjectNotFoundException("projectId", id));

        // When/Then
        mockMvc.perform(put("/api/projects/{projectId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project is not found with projectId: 9")));

        verify(projectService).updateProject(id, projectUpdateDTO);
    }

    @Test
    @DisplayName("DeleteProject: Project is deleted")
    void testDeleteProject() throws Exception {
        // Given
        Long id = 2L;
        doNothing().when(projectService).deleteProject(id);

        // When/Then
        mockMvc.perform(delete("/api/projects/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(id);
    }

}
