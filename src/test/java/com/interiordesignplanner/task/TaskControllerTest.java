package com.interiordesignplanner.task;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectRepository;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.room.RoomRepository;
import com.interiordesignplanner.room.RoomType;
import com.interiordesignplanner.room.RoomUpdateDTO;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName(value = "Task Controller Test Suite")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Converts the RoomDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerRepository designerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Project project1, project2, project3;
    private RoomUpdateDTO roomUpdateDTO;
    private Task task, task2, task3, task4;
    private List<Task> checkList1, checkList2;
    private Designer designer;
    private Room room1, room2;

    @BeforeEach
    void setUp() {

        roomRepository.deleteAll();
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

        Client client1 = new Client();
        client1.setFirstName("John");
        client1.setLastName("Moss");
        client1.setEmailAddress("johnmoss@gmail.com");
        client1.setPhoneNumber("07894832061");
        client1.setAddress("4B Avenue, Reading, R6 6E3");
        client1.setNotes("Prefers eco-friendly materials");
        client1.setDesigner(designer);
        clientRepository.save(client1);

        project1 = new Project();
        project1.setClient(client1);
        project1.setProjectName("Coastal Escape");
        project1.setStatus(ProjectStatus.PLANNING);
        project1.setBudget(BigDecimal.valueOf(23000));
        project1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        project1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project1.setStartDate(LocalDate.of(2025, 07, 20));
        project1.setDueDate(LocalDate.of(2026, 01, 25));

        project2 = new Project();
        project2.setClient(client1);
        project2.setProjectName("Modern Living Room");
        project2.setStatus(ProjectStatus.ACTIVE);
        project2.setBudget(BigDecimal.valueOf(6000));
        project2.setDescription("Remove old tiles and wallpaper");
        project2.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project2.setStartDate(LocalDate.of(2026, 5, 20));
        project2.setDueDate(LocalDate.of(2026, 9, 25));

        project3 = new Project();
        project3.setClient(client1);
        project3.setProjectName("Scandinavian Loft");
        project3.setBudget(BigDecimal.valueOf(8000.00));
        project3.setStatus(ProjectStatus.PLANNING);
        project3.setDescription("Light lighting and open space");
        project3.setMeetingURL("https://meet.google.com/7tf-do9-34s");
        project3.setStartDate(LocalDate.of(2025, 07, 20));
        project3.setDueDate(LocalDate.of(2026, 10, 25));

        task = new Task();
        task.setTaskName("Flooring");
        task.setTask("Remove floor tiles in the Kitchen");
        task.setDate(LocalDate.of(2026, 3, 10));

        task2 = new Task();
        task2.setTaskName("Order Tiles");
        task2.setTask("Wall tiles from Wickes");
        task2.setDate(LocalDate.of(2026, 3, 10));

        task3 = new Task();
        task3.setTaskName("Contractors");
        task3.setTask("Call contractors before next meeting");
        task3.setDate(LocalDate.of(2026, 5, 10));

        task4 = new Task();
        task4.setTaskName("Curtain");
        task4.setTask("Order swatches for curtain fitting");
        task4.setDate(LocalDate.of(2026, 3, 10));

        checkList1 = new ArrayList<>();
        checkList2 = new ArrayList<>();

        room1 = new Room();
        room1.setProject(project1);
        room1.setType(RoomType.KITCHEN);
        room1.setWidth(5.2);
        room1.setHeight(4.0);
        room1.setLength(4.5);
        room1.setUnit("m");
        room1.setChecklist(checkList1);

        room2 = new Room();
        room2.setProject(project2);
        room2.setType(RoomType.LIVING_ROOM);
        room2.setWidth(6.4);
        room2.setHeight(3.0);
        room2.setLength(7.5);
        room2.setUnit("m");
        room2.setChecklist(checkList2);

        project1.setRoom(room1);
        project2.setRoom(room2);

        RoomDTO roomDTO1 = new RoomDTO();
        roomDTO1.setProjectName("Coastal Escape");
        roomDTO1.setType(RoomType.KITCHEN);
        roomDTO1.setWidth(5.2);
        roomDTO1.setHeight(4.0);
        roomDTO1.setLength(4.5);
        roomDTO1.setUnit("m");
        roomDTO1.setChecklist(checkList1);

        RoomDTO roomDTO2 = new RoomDTO();
        roomDTO2.setProjectName("Modern Living Room");
        roomDTO2.setType(RoomType.LIVING_ROOM);
        roomDTO2.setWidth(6.4);
        roomDTO2.setHeight(3.0);
        roomDTO2.setLength(7.5);
        roomDTO2.setUnit("m");
        roomDTO2.setChecklist(checkList2);

        checkList1.add(task);
        checkList1.add(task2);

        checkList2.add(task3);
        checkList2.add(task4);

        roomRepository.save(room1);
        roomRepository.save(room2);
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

    }

    @Test
    @DisplayName("AddTask: Created a new Task")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddTask() throws Exception {
        // Given

        Task newTask = new Task();
        newTask.setTaskName("Order Sofa");
        newTask.setTask("Find a green or blue sofa");
        newTask.setDate(LocalDate.of(2026, 3, 15));

        checkList1.add(newTask);

        // When/Then
        mockMvc.perform(patch("/api/rooms/{roomId}/task", room1
                .getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checklist[2].taskName", is("Order Sofa")));

    }

    @Test
    @DisplayName("EditTask: Edited Task")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testEditTask() throws Exception {
        // Given

        task.setTaskName("Kitchen Flooring");

        int index = 0;

        checkList1.set(index, task);

        // When/Then
        mockMvc.perform(patch("/api/rooms/{roomId}/task/{index}", room1
                .getId(), index)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checklist[0].taskName", is("Kitchen Flooring")))
                .andExpect(jsonPath(
                        "$.checklist[0].task",
                        is("Remove floor tiles in the Kitchen")));

    }

    @Test
    @DisplayName("DeleteTask: Task is deleted")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteTask() throws Exception {
        // Given
        int index = 1;

        // When/Then
        mockMvc.perform(delete("/api/rooms/{id}/task/{index}", room2.getId(), index)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

}
