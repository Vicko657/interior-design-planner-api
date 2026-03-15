package com.interiordesignplanner.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectService;
import com.interiordesignplanner.project.ProjectStatus;

/**
 * Unit tests for {@link RoomService}.
 *
 * <p>
 * Ensures room creation, updates, and associations with projects
 * work as intended. Tests validation on room type, dimensions, and
 * relationship handling with checklists and changes.
 * </p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Room Service Test Suite")
public class RoomServiceTest {

    // Mock room repository
    @Mock
    private RoomRepository roomRepository;

    // Project mapper
    @Autowired
    private RoomMapper roomMapper;

    // Mock room service
    @InjectMocks
    private RoomService roomService;

    // Mock project service
    @Mock
    private ProjectService projectService;

    private Client client1;

    private Project project1, project2;

    private Room room1, room2;

    private Task task, task2, task3, task4;

    private Item item, item2;

    private List<Task> checkList1, checkList2;
    private List<Item> inventory1, inventory2;

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
        roomService = new RoomService(roomRepository, projectService, roomMapper);

        client1 = new Client();
        client1.setId(1L);

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
        project2.setProjectName("Industrial Hallway Redesign");
        project2.setStatus(ProjectStatus.ACTIVE);
        project2.setBudget(BigDecimal.valueOf(1000.00));
        project2.setDescription("Remove old tiles and wallpaper");
        project2.setMeetingURL("https://meet.google.com/hyd-ken-csa");
        project2.setStartDate(LocalDate.of(2026, 5, 20));
        project2.setDueDate(LocalDate.of(2026, 9, 25));

        checkList1 = new ArrayList<>();
        inventory1 = new ArrayList<>();

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

        room1 = new Room();
        room1.setId(1L);
        room1.setProject(project1);
        room1.setType(RoomType.BEDROOM);
        room1.setHeight(4.5);
        room1.setLength(6.7);
        room1.setWidth(4.0);
        room1.setInventory(inventory1);
        room1.setChecklist(checkList1);
        room1.setUnit("m");

        checkList1.add(task);
        checkList1.add(task2);
        inventory1.add(item2);

        checkList2 = new ArrayList<>();
        inventory2 = new ArrayList<>();

        room2 = new Room();
        room2.setId(1L);
        room2.setProject(project2);
        room2.setType(RoomType.HALLWAY);
        room2.setHeight(5.8);
        room2.setLength(6.2);
        room2.setWidth(2.3);
        room2.setInventory(inventory2);
        room2.setChecklist(checkList2);
        room2.setUnit("m");

        checkList2.add(task3);
        checkList2.add(task4);
        inventory2.add(item);

    }

    /**
     * Tests for checking if Get all Rooms returns a list of rooms
     */
    @Test
    @DisplayName("GetAllRooms: Returns all of the projects in the database")
    public void testGetAllRooms_ReturnsAllRooms() {
        // Arrange: A list created with rooms and mock Repository to test if all
        // rooms are returned

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        // Act: Query the service layer the if all projects are returnes
        List<RoomDTO> result = roomService.getAllRooms();

        // Assert: Verifies that the result is not null and projects are retrieved
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertThat(result).extracting(RoomDTO::getType).containsExactly(RoomType.BEDROOM, RoomType.HALLWAY);
        verify(roomRepository).findAll();
        verifyNoMoreInteractions(roomRepository);

    }

    /**
     * Tests for checking if Get all rooms returns a empty list
     */
    @Test
    @DisplayName("GetAllRooms: Returns empty list")
    public void testGetAllRooms_ReturnsEmptyList() {
        // Arrange: Empty list is created and Mock Repository to test if it returns a
        // empty list
        List<Room> rooms = Collections.emptyList();
        when(roomRepository.findAll()).thenReturn(rooms);

        // Act: Query the service layer the if a empty list is returned
        List<RoomDTO> result = roomService.getAllRooms();

        // Assert: Verifies that the result empty
        assertThat(result).isEqualTo(rooms);
    }

    /**
     * Tests for when the room is found with the room id
     */
    @Test
    @DisplayName("GetRoom: Returns project by ID")
    public void testGetRoom_ReturnsRoom() {
        // Arrange: Sets the roomId and mocks the repository
        Long roomId = 1L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));

        // Act: Query the service layer to return the room with the id
        RoomDTO result = roomService.getRoomById(roomId);

        // Assert: Verifies that the result is not null and a room with the same Id
        // is
        // returned
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(roomId);
        assertThat(result.getProjectName()).isEqualTo("Industrial Loft Redesign");
        assertThat(result.getChecklist().get(0).getTaskName()).isEqualTo("Flooring");
    }

    /**
     * Tests for when the Room is not found, returns a empty set and throws a
     * RoomNotFoundException
     */
    @Test
    @DisplayName("GetRoom: Room ID is not found")
    public void testGetRoom_ReturnsNotFound() {
        // Arrange: Set the roomId and mock the repository
        Long roomId = 3L;
        String errorMessage = "Room is not found with " + "roomId" + ": " + roomId;

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown
        RoomNotFoundException exception = assertThrows(RoomNotFoundException.class, () -> {
            roomService.getRoomById(roomId);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    /**
     * Tests for creating a new Room successfully
     */
    @Test
    @DisplayName("CreateRoom: Adds a new Room")
    public void testCreateRoom_ReturnsCreated() {

        // Arrange: Mock Repository to test if a new Room has been created

        Project project3 = new Project();
        project3.setId(7L);

        RoomCreateDTO roomDTO = new RoomCreateDTO(project3, RoomType.BATHROOM, 4.5, 6.5, 4.0, "m");

        Room savedRoom = new Room();
        savedRoom.setId(3L);
        savedRoom.setProject(project3);
        savedRoom.setType(RoomType.BATHROOM);
        savedRoom.setHeight(6.5);
        savedRoom.setLength(4.5);
        savedRoom.setWidth(4.0);
        savedRoom.setUnit("m");

        when(projectService.findProject(7L)).thenReturn(project1);
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        // Act: Query the service layer the if room is there
        RoomDTO result = roomService.addRoom(roomDTO, project3.getId());

        // Assert: Verifies that the result is not null and room has been created
        assertNotNull(result);
        assertThat(result).extracting(RoomDTO::getWidth).isEqualTo(4.0);
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests for updating a Room
     */
    @Test
    @DisplayName("UpdateRoom: Updates Room details")
    public void testUpdateRoom_ReturnsUpdated() {
        // Arrange: Sets the roomId and mocks the repository
        Long roomId = 2L;

        // Updated Room Type
        RoomUpdateDTO updatedRoom = new RoomUpdateDTO();
        updatedRoom.setType(RoomType.LIVING_ROOM);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room2));
        when(roomRepository.save(room2)).thenReturn(room2);

        // Act: Query the service layer to return the Room with the id and update the
        // Room's details
        RoomDTO result = roomService.updateRoom(roomId, updatedRoom);

        // Assert: Verifies that the Room was updated
        assertNotNull(result);
        assertEquals(result.getType(), RoomType.LIVING_ROOM);
        verify(roomRepository).findById(roomId);

    }

    /**
     * Tests for updating a Room and the Room is not found
     */
    @Test
    @DisplayName("UpdateRoom: Room ID is not found")
    public void testUpdateRoom_ReturnsNotFound() {
        // Arrange: Sets the RoomId and mocks the repository

        Long roomId = 2L;
        String errorMessage = "Room is not found with " + "roomId" + ": " + roomId;

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        RoomUpdateDTO updateRoom = new RoomUpdateDTO();
        updateRoom.setHeight(4.0);

        // Act: Queries if the exception is thrown if Room is not found when updating
        RoomNotFoundException exception = assertThrows(RoomNotFoundException.class, () -> {
            roomService.updateRoom(roomId, updateRoom);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).save(null);

    }

    /**
     * Tests for deleting a Room
     */
    @Test
    @DisplayName("DeleteRoom: Deletes Room details")
    public void testDeleteRoom_ReturnsDeleted() {
        // Arrange: Sets the roomId and mocks the repository
        Long roomId = 2L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room2));

        // Act: Query the service layer to return the Room with the id and delete the
        // Room
        roomService.deleteRoom(roomId);

        // Assert: Verifies that the Room was deleted and is not found
        verify(roomRepository).delete(room2);
        verify(roomRepository).findById(roomId);

    }

    /**
     * Tests for deleting a Room and the Room is not found
     */
    @Test
    @DisplayName("DeleteRoom: Room ID is not found")
    public void testDeleteRoom_ReturnsNotFound() {
        // Arrange: Sets the roomId sand mocks the repository
        Long roomId = 4L;
        String errorMessage = "Room is not found with " + "roomId" + ": " + roomId;
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act: Queries if the exception is thrown if room is not found when deleting
        RoomNotFoundException exception = assertThrows(RoomNotFoundException.class, () -> {
            roomService.deleteRoom(roomId);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).delete(any());

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

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new task, saves room
        RoomDTO result = roomService.addTask(roomId, newTask);

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

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room2));
        when(roomRepository.save(room2)).thenReturn(room2);

        // Act: Query the service layer the if room exists, adds a new task, saves room
        RoomDTO result = roomService.editTask(roomId, task4, index);

        // Assert: Verifies that the result is not null and task has been updated
        assertNotNull(result);
        assertEquals(room2.getInventory().size(), 1);
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

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer to return the Room with the id and delete the
        // task and save the room
        roomService.deleteTask(roomId, index);

        // Assert: Verifies that the task was deleted, the size of the list is now 1
        assertEquals(room1.getChecklist().size(), 1);
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests adding a new Item successfully
     */
    @Test
    @DisplayName("AddItem: Adds new Item")
    public void testAddItem_ReturnsUpdatedList() {

        // Arrange: Mock Repository to test if a new Item has been created

        Long roomId = room1.getId();

        Item newItem = new Item();
        newItem.setImageUrl("/img/product3.png");
        newItem.setItemName("Barstools");
        newItem.setDescription(
                "Designed with a sumptuous velvet seat and a sturdy metal frame, this barstool offers both luxury and durability.");
        newItem.setPrice(BigDecimal.valueOf(152.00));
        newItem.setQuantity(2);
        newItem.setDimensions("W: 47, D: 51, H: 88cm");
        newItem.setLink(
                "https://dusk.com/products/mollie-set-of-2-barstools-cappuccino?variant=55388585918842&gad_source=1&gad_campaignid=21757503987&gbraid=0AAAAADNOeOVm_QYZzEg2oFlbs2I2wuZmD&gclid=CjwKCAjwjtTNBhB0EiwAuswYhjSsFcuAfKf4TY-c07OEm4GAnFZXefbe5Uv5vgGlEPwFGe4lq3lmUxoCJbIQAvD_BwE");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new item, saves room
        RoomDTO result = roomService.addItem(roomId, newItem);

        // Assert: Verifies that the result is not null and item has been added
        assertNotNull(result);
        assertEquals(room1.getInventory().size(), 2);
        assertThat(result.getInventory().get(1).getPrice()).isEqualTo(BigDecimal.valueOf(152.00));
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests editing a Item successfully
     */
    @Test
    @DisplayName("EditItem: Edits Item")
    public void testEditItem_ReturnsUpdatedItem() {

        // Arrange: Mock Repository to test if the room found Item has been updated

        Long roomId = room1.getId();

        int index = 0;

        item.setPrice(BigDecimal.valueOf(239.80));
        item.setQuantity(2);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new item, saves room
        RoomDTO result = roomService.editItem(roomId, item, index);

        // Assert: Verifies that the result is not null and item has been updated
        assertNotNull(result);
        assertEquals(room1.getInventory().size(), 1);
        assertThat(result.getInventory().get(0).getPrice()).isEqualTo(BigDecimal.valueOf(239.80));
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    /**
     * Tests for removing a item
     */
    @Test
    @DisplayName("DeleteItem: Remove Item")
    public void testDeleteItem_ReturnsDeleted() {
        // Arrange: Sets the roomId and index of the item to be removed and mocks the
        // repository
        Long roomId = 1L;
        int index = 0;

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room1));
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer to return the Room with the id and delete the
        // item and save the room
        roomService.deleteItem(roomId, index);

        // Assert: Verifies that the task was deleted, the size of the list is now 0
        assertEquals(room1.getInventory().size(), 0);
        verify(roomRepository, times(1)).save(any(Room.class));

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(roomRepository);
    }
}
