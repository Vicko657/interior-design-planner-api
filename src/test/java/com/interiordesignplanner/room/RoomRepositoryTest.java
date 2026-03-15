package com.interiordesignplanner.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interiordesignplanner.project.Project;

/**
 * Unit tests for {@link RoomRepository}.
 *
 * <p>
 * This class verifies the persistence and retrieval of {@link Room} entities.
 * It focuses on repository-level behavior including:
 * <p>
 * The tests use mocked repository behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Room Repository Test Suite")
public class RoomRepositoryTest {

    // Mock room repository
    @Mock
    private RoomRepository roomRepository;

    private Room room;

    private Project project;

    private Task task;

    private Item item;

    private List<Task> checkList;
    private List<Item> inventory;

    @BeforeEach
    public void setUp() {

        checkList = new ArrayList<>();
        inventory = new ArrayList<>();

        task = new Task();
        task.setTaskName("Flooring");
        task.setTask("Remove floor tiles in the Kitchen");
        task.setDate(LocalDate.of(2026, 3, 10));

        item = new Item();
        item.setImageUrl("/img/product1.png");
        item.setItemName("Coffee Table");
        item.setDescription(
                "Finished in chalked solid mango wood the Imogen coffee table features an oval table top and chunky curved legs. The chalked mango wood finish adds texture and shows the natural wood grain for a rustic look.");
        item.setDimensions("H45cm W110cm D55cm");
        item.setOrdered(true);
        item.setPrice(BigDecimal.valueOf(119.99));
        item.setQuantity(1);

        checkList.add(task);
        inventory.add(item);

        project = new Project();

        room = new Room();
        room.setId(1L);
        room.setType(RoomType.BEDROOM);
        room.setHeight(4.0);
        room.setLength(6.7);
        room.setWidth(4.5);
        room.setProject(project);
        room.setInventory(inventory);
        room.setUnit("m");
        room.setChecklist(checkList);

    }

    /**
     * Tests if the Room can be found by their type
     */
    @Test
    @DisplayName("GetRoomsByType: Finds rooms by type")
    public void testGetRoomsByType_ReturnsProjects() {

        // Arrange: Mock repository to return test for type (BEDROOM).
        when(roomRepository.findRoomsByType(RoomType.BEDROOM)).thenReturn(List.of(room));

        // Act: Query the repository with type (BEDROOM)
        List<Room> result = roomRepository.findRoomsByType(RoomType.BEDROOM);

        // Assert: Verify that the result does returns one test and is (BEDROOM)
        assertNotNull(result);
        assertThat(1).isEqualTo(result.size());
        assertThat(room).isEqualTo(result.get(0));
        verify(roomRepository).findRoomsByType(RoomType.BEDROOM);

    }

    /**
     * Tests when the Room type is invalid and is not found
     */
    @Test
    @DisplayName("GetRoomsByType: Room is not found with type")
    public void testGetByStatus_ReturnsEmptyList() {

        // Arrange: Mock repository to test a different type (DINING_ROOM).
        when(roomRepository.findRoomsByType(RoomType.DINING_ROOM)).thenReturn(Collections.emptyList());

        // Act: Query the repository with type (BEDROOM)
        List<Room> result = roomRepository.findRoomsByType(RoomType.DINING_ROOM);

        // Assert: Verify that the result doesnt return test
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomRepository).findRoomsByType(RoomType.DINING_ROOM);

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(roomRepository);
    }

}
