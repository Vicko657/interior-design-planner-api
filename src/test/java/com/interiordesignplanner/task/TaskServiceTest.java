package com.interiordesignplanner.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectDTO;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.room.RoomRepository;
import com.interiordesignplanner.room.RoomService;
import com.interiordesignplanner.room.RoomType;
import com.interiordesignplanner.task.Task;

@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Task Service Test Suite")
public class TaskServiceTest {

    // Mock room repository
    @Mock
    private RoomRepository roomRepository;

    // Project mapper
    @Autowired
    private RoomMapper roomMapper;

    // Mock task service
    @InjectMocks
    private TaskService taskService;

    // Mock room service
    @Mock
    private RoomService roomService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private DesignerService designerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Client client1;
    private User user, admin;
    private Designer designer;
    private Project project1, project2;
    private Room room1, room2;
    private Task task, task2, task3, task4;
    private List<Task> checkList1, checkList2;

    @BeforeEach
    // Created mock room tests
    public void setUp() {

        // Added Room Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Room.class, RoomDTO.class).setPostConverter(convert -> {
            Room source = convert.getSource();
            RoomDTO destination = convert.getDestination();
            if (source.getProject() != null) {
                destination.setProjectName((source.getProject().getProjectName()));
            }
            return destination;
        });

        roomMapper = new RoomMapper(modelMapper);
        taskService = new TaskService(roomRepository, roomService, roomMapper, authenticationService, designerService);

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

        project1 = new Project();
        project1.setId(1L);
        project1.setClient(client1);
        project1.setProjectName("Luxury Master Bedroom");
        project1.setStatus(ProjectStatus.PLANNING);
        project1.setBudget(BigDecimal.valueOf(20000.00));
        project1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
        project1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project1.setStartDate(LocalDate.of(2025, 07, 20));
        project1.setDueDate(LocalDate.of(2026, 01, 25));

        project2 = new Project();
        project2.setId(2L);
        project2.setClient(client1);
        project2.setProjectName("Industrial Hallway Redesign");
        project2.setStatus(ProjectStatus.ACTIVE);
        project2.setBudget(BigDecimal.valueOf(1000.00));
        project2.setDescription("Remove old tiles and wallpaper");
        project2.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project2.setStartDate(LocalDate.of(2026, 5, 20));
        project2.setDueDate(LocalDate.of(2026, 9, 25));

        checkList1 = new ArrayList<>();

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

        room1 = new Room();
        room1.setId(1L);
        room1.setProject(project1);
        room1.setType(RoomType.BEDROOM);
        room1.setHeight(4.5);
        room1.setLength(6.7);
        room1.setWidth(4.0);
        room1.setChecklist(checkList1);
        room1.setUnit("m");

        checkList1.add(task);
        checkList1.add(task2);

        checkList2 = new ArrayList<>();

        room2 = new Room();
        room2.setId(2L);
        room2.setProject(project2);
        room2.setType(RoomType.HALLWAY);
        room2.setHeight(5.8);
        room2.setLength(6.2);
        room2.setWidth(2.3);
        room2.setChecklist(checkList2);
        room2.setUnit("m");

        checkList2.add(task3);
        checkList2.add(task4);

    }

    /**
     * Tests returning all Task successfully
     */
    @Test
    @DisplayName("GetTasks: Returns tasks")
    public void testGetTasks_ReturnsPage() {

        // Arrange: A page created with tasks, pageable and mock Repository to test if
        // all the designer's tasks are returned

        TaskDTO task1 = new TaskDTO();
        task1.setTaskName("Bed");
        task1.setTask("Find a double sized bed with a wooden frame");
        task1.setDate(LocalDate.of(2026, 4, 5));
        task1.setProjectName("Luxury Master Bedroom");

        TaskDTO task2 = new TaskDTO();
        task2.setTaskName("Order Tiles");
        task2.setTask("Wall tiles from Wickes");
        task2.setDate(LocalDate.of(2026, 3, 10));
        task2.setProjectName("Industrial Hallway Redesign");

        TaskDTO task3 = new TaskDTO();
        task3.setTaskName("Contractors");
        task3.setTask("Call contractors before next meeting");
        task3.setDate(LocalDate.of(2026, 5, 10));
        task3.setProjectName("Industrial Hallway Redesign");

        Pageable pageable = PageRequest.of(0, 10);

        List<TaskDTO> tasks = new ArrayList<>();

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        Page<TaskDTO> mockPage = new PageImpl<>(tasks);

        when(authenticationService.findUser("sam")).thenReturn(user);

        when(designerService.findDesigner(user.getId())).thenReturn(designer);

        when(roomRepository.findTasks(user.getId(), pageable)).thenReturn(mockPage);
        ;

        // Act: Query the service layer the if all the designer's tasks are returned
        Page<TaskDTO> result = taskService.getTasks(user.getUsername(), pageable);

        // Assert: Verifies that the result is not null and tasks are retrieved
        assertNotNull(result);
        assertEquals(result.getSize(), 3);
        assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2026, 4, 5));
        assertThat(result.getContent().get(2).getProjectName()).isEqualTo("Industrial Hallway Redesign");

    }

    /**
     * Tests adding a new Task successfully
     */
    @Test
    @DisplayName("AddTask: Adds new Task")
    public void testAddTask_ReturnsUpdatedList() {

        // Arrange: Mock Repository to test if a new Task has been created

        Long roomId = room1.getId();

        Task newTask = new Task();
        newTask.setTaskName("Bed");
        newTask.setTask("Find a double sized bed with a wooden frame");
        newTask.setDate(LocalDate.of(2026, 4, 5));

        when(roomService.findRoom(1L)).thenReturn(room1);
        roomService.findRoomByDesigner(room1, "sam");

        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new task, saves room
        RoomDTO result = taskService.addTask(roomId, newTask, user.getUsername());

        // Assert: Verifies that the result is not null and task has been created
        assertNotNull(result);
        assertEquals(room1.getChecklist().size(), 3);
        assertThat(result.getChecklist().get(2).getDate()).isEqualTo(LocalDate.of(2026, 4, 5));
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests editing a task successfully
     */
    @Test
    @DisplayName("EditTask: Edits Task")
    public void testEditTask_ReturnsUpdatedTask() {

        // Arrange: Mock Repository to test if the room found Task has been updated
        Long roomId = room2.getId();

        int index = 1;

        task4.setDate(LocalDate.of(2026, 3, 10));

        when(roomService.findRoom(2L)).thenReturn(room2);
        when(roomRepository.save(room2)).thenReturn(room2);

        // Act: Query the service layer the if room exists, adds a new task, saves room
        RoomDTO result = taskService.editTask(roomId, task4, index, user.getUsername());

        // Assert: Verifies that the result is not null and task has been updated
        assertNotNull(result);
        assertThat(result.getChecklist().get(1).getDate()).isEqualTo(LocalDate.of(2026, 3, 10));
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests for removing a task
     */
    @Test
    @DisplayName("DeleteTask: Remove Task")
    public void testDeleteTask_ReturnsDeleted() {
        // Arrange: Sets the roomId and index of the task to be removed and mocks the
        // repository
        Long roomId = 1L;
        int index = 1;

        when(roomService.findRoom(1L)).thenReturn(room1);
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer to return the Room with the id and delete the
        // task and save the room
        taskService.deleteTask(roomId, index, user.getUsername());

        // Assert: Verifies that the task was deleted, the size of the list is now 1
        assertEquals(room1.getChecklist().size(), 1);
        verify(roomRepository, times(1)).save(any(Room.class));

    }

}
