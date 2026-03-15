package com.interiordesignplanner.room;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RoomController.class)
@DisplayName(value = "Room Controller Test Suite")
public class RoomControllerTest {

        @Autowired
        private MockMvc mockMvc;

        // Converts the RoomDTO into JSON
        @Autowired
        private ObjectMapper objectMapper;

        // Mockito Bean replaces MockBean(depreciated)
        @MockitoBean
        private RoomService roomService;

        private RoomDTO roomDTO1, roomDTO2;

        private RoomCreateDTO roomCreateDTO, roomCreateDTO2;

        private RoomUpdateDTO roomUpdateDTO;

        private Project project1, project2;

        private Client client1;

        private Task task, task2, task3, task4;

        private Item item, item2;

        private List<Task> checkList1, checkList2;
        private List<Item> inventory1, inventory2;

        @BeforeEach
        void setUp() {

                client1 = new Client();
                client1.setId(7L);
                client1.setFirstName("John");
                client1.setLastName("Moss");
                client1.setEmail("johnmoss@gmail.com");
                client1.setPhone("07894832061");
                client1.setAddress("4B Avenue, Reading, R6 6E3");
                client1.setNotes("");

                project1 = new Project();
                project1.setId(6L);
                project1.setClient(client1);
                project1.setProjectName("Coastal Escape");
                project1.setStatus(ProjectStatus.PLANNING);
                project1.setBudget(BigDecimal.valueOf(23000));
                project1.setDescription("Exposed brick walls, metal fixtures, and reclaimed wood accents");
                project1.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                project1.setStartDate(LocalDate.of(2025, 07, 20));
                project1.setDueDate(LocalDate.of(2026, 01, 25));

                project2 = new Project();
                project2.setId(8L);
                project2.setClient(client1);
                project2.setProjectName("Modern Living Room");
                project2.setStatus(ProjectStatus.ACTIVE);
                project2.setBudget(BigDecimal.valueOf(6000));
                project2.setDescription("Remove old tiles and wallpaper");
                project2.setMeetingURL("https://meet.google.com/hyd-ken-csa");
                project2.setStartDate(LocalDate.of(2026, 5, 20));
                project2.setDueDate(LocalDate.of(2026, 9, 25));

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
                                "Finished in chalked solid mango wood the Imogen coffee table features an oval table top and chunky curved legs.");
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

                checkList1 = new ArrayList<>();
                inventory1 = new ArrayList<>();

                checkList2 = new ArrayList<>();
                inventory2 = new ArrayList<>();

                roomCreateDTO = new RoomCreateDTO();
                roomCreateDTO.setType(RoomType.KITCHEN);
                roomCreateDTO.setWidth(5.2);
                roomCreateDTO.setHeight(4.0);
                roomCreateDTO.setLength(4.5);
                roomCreateDTO.setUnit("m");

                roomDTO1 = new RoomDTO();
                roomDTO1.setId(1L);
                roomDTO1.setProjectName("Coastal Escape");
                roomDTO1.setType(RoomType.KITCHEN);
                roomDTO1.setWidth(5.2);
                roomDTO1.setHeight(4.0);
                roomDTO1.setLength(4.5);
                roomDTO1.setUnit("m");
                roomDTO1.setInventory(inventory1);
                roomDTO1.setChecklist(checkList1);

                roomDTO2 = new RoomDTO();
                roomDTO2.setId(2L);
                roomDTO2.setProjectName("Modern Living Room");
                roomDTO2.setType(RoomType.LIVING_ROOM);
                roomDTO2.setWidth(6.4);
                roomDTO2.setHeight(3.0);
                roomDTO2.setLength(7.5);
                roomDTO2.setUnit("m");
                roomDTO2.setInventory(inventory2);
                roomDTO2.setChecklist(checkList2);

                checkList1.add(task);
                checkList1.add(task2);
                inventory1.add(item2);

                checkList2.add(task3);
                checkList2.add(task4);
                inventory2.add(item);

        }

        @Test
        @DisplayName("GetAllRooms: Should return all Rooms")
        void testGetAllRooms() throws Exception {

                // Given
                when(roomService.getAllRooms()).thenReturn(List.of(roomDTO1, roomDTO2));

                // When/Then
                mockMvc.perform(get("/api/rooms")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].type", is(
                                                "KITCHEN")))
                                .andExpect(jsonPath("$[1].projectName", is("Modern Living Room")));

                verify(roomService).getAllRooms();
        }

        @Test
        @DisplayName("GetRoomById: Should return a Room")
        void testGetRoomById() throws Exception {
                // Given
                when(roomService.getRoomById(2L)).thenReturn(roomDTO2);

                // When/Then
                mockMvc.perform(get("/api/rooms/2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(2)))
                                .andExpect(jsonPath("$.width", is(
                                                6.4)))
                                .andExpect(jsonPath("$.checklist[0].taskName",
                                                is("Contractors")));

                verify(roomService).getRoomById(2L);
        }

        @Test
        @DisplayName("GetRoomById: Room Not Found")
        void testGetRoomById_NotFound() throws Exception {
                // Given
                when(roomService.getRoomById(80L)).thenThrow(new RoomNotFoundException("roomId", 80L));

                // When/Then
                mockMvc.perform(get("/api/rooms/80")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Room is not found with roomId: 80")));

                verify(roomService).getRoomById(80L);
        }

        @Test
        @DisplayName("AddRoom: Room is created")
        void testAddRoom() throws Exception {
                // Given

                Long projectId = project1.getId();
                when(roomService.addRoom(roomCreateDTO, projectId)).thenReturn(roomDTO1);

                // When/Then
                mockMvc.perform(post("/api/rooms/{projectId}", projectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roomDTO1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.type", is("KITCHEN")));

                verify(roomService).addRoom(roomCreateDTO, projectId);
        }

        @Test
        @DisplayName("AddRoom: Missing width")
        void testAddRoom_ValidationFailure() throws Exception {
                // Given
                roomCreateDTO2 = new RoomCreateDTO();
                roomCreateDTO2.setType(RoomType.LIVING_ROOM);
                roomCreateDTO2.setProject(project2);
                roomCreateDTO2.setHeight(3.0);
                roomCreateDTO2.setLength(7.5);
                roomCreateDTO2.setUnit("m");

                // When/Then
                mockMvc.perform(post("/api/rooms/{projectId}", project2.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roomCreateDTO2)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.width", is("Width is required")));

                verify(roomService, never()).addRoom(roomCreateDTO, project2.getId());
        }

        @Test
        @DisplayName("UpdateRoom: Room's Due Date is updated")
        void testUpdateRoom() throws Exception {
                // Given
                Long id = roomDTO2.getId();
                roomUpdateDTO = new RoomUpdateDTO();
                roomUpdateDTO.setType(RoomType.BATHROOM);

                roomDTO2.setType(RoomType.BATHROOM);

                when(roomService.updateRoom(id, roomUpdateDTO)).thenReturn(roomDTO2);

                // When/Then
                mockMvc.perform(put("/api/rooms/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roomUpdateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.type", is("BATHROOM")));

                verify(roomService).updateRoom(id, roomUpdateDTO);
        }

        @Test
        @DisplayName("UpdateRoom: Room not found")
        void testUpdateRoom_NotFound() throws Exception {
                // Given
                Long id = 9L;
                roomUpdateDTO = new RoomUpdateDTO();
                when(roomService.updateRoom(id, roomUpdateDTO))
                                .thenThrow(new RoomNotFoundException("roomId", id));

                // When/Then
                mockMvc.perform(put("/api/rooms/{roomId}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roomUpdateDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Room is not found with roomId: 9")));

                verify(roomService).updateRoom(id, roomUpdateDTO);
        }

        @Test
        @DisplayName("DeleteRoom: Room is deleted")
        void testDeleteRoom() throws Exception {
                // Given
                Long id = 2L;
                doNothing().when(roomService).deleteRoom(id);

                // When/Then
                mockMvc.perform(delete("/api/rooms/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(roomService).deleteRoom(id);
        }

        @Test
        @DisplayName("AddTask: Created a new Task")
        void testAddTask() throws Exception {
                // Given

                Task newTask = new Task();
                newTask.setTaskName("Order Sofa");
                newTask.setTask("Find a green or blue sofa");
                newTask.setDate(LocalDate.of(2026, 3, 15));

                Long roomId = roomDTO1.getId();

                checkList1.add(newTask);

                roomDTO1.setChecklist(checkList1);

                when(roomService.addTask(roomId, newTask)).thenReturn(roomDTO1);

                // When/Then
                mockMvc.perform(patch("/api/rooms/{roomId}/task", roomId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                newTask)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.checklist[2].taskName", is("Order Sofa")));

                verify(roomService).addTask(roomId, newTask);
        }

        @Test
        @DisplayName("EditTask: Edited Task")
        void testEditTask() throws Exception {
                // Given

                task.setTaskName("Kitchen Flooring");

                Long roomId = roomDTO1.getId();

                int index = 0;

                checkList1.set(index, task);

                roomDTO1.setChecklist(checkList1);

                when(roomService.editTask(roomId, task, index)).thenReturn(roomDTO1);

                // When/Then
                mockMvc.perform(patch("/api/rooms/{roomId}/task/{index}", roomId, index)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                task)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.checklist[0].taskName", is("Kitchen Flooring")))
                                .andExpect(jsonPath(
                                                "$.checklist[0].task",
                                                is("Remove floor tiles in the Kitchen")));

                verify(roomService).editTask(roomId, task, index);
        }

        @Test
        @DisplayName("DeleteTask: Task is deleted")
        void testDeleteTask() throws Exception {
                // Given
                Long id = 2L;
                int index = 1;
                doNothing().when(roomService).deleteTask(id, index);

                // When/Then
                mockMvc.perform(delete("/api/rooms/{id}/task/{index}", id, index)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(roomService).deleteTask(id, index);
        }

        @Test
        @DisplayName("AddItem: Created a new Item")
        void testAddItem() throws Exception {
                // Given

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
                newItem.setOrdered(false);

                Long roomId = roomDTO1.getId();

                inventory1.add(newItem);

                roomDTO1.setInventory(inventory1);

                when(roomService.addItem(roomId, newItem)).thenReturn(roomDTO1);

                // When/Then
                mockMvc.perform(patch("/api/rooms/{roomId}/inventory", roomId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                newItem)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.inventory[1].itemName", is("Barstools")));

                verify(roomService).addItem(roomId, newItem);
        }

        @Test
        @DisplayName("EditItem: Edited Item")
        void testEditItem() throws Exception {
                // Given

                item.setLink("https://www.laura-james.co.uk/products/imogen-110cm-coffee-table-chalked-mangowood");

                Long roomId = roomDTO2.getId();

                int index = 0;

                inventory2.set(index, item);

                roomDTO2.setInventory(inventory2);

                when(roomService.editItem(roomId, item, index)).thenReturn(roomDTO2);

                // When/Then
                mockMvc.perform(patch("/api/rooms/{roomId}/inventory/{index}", roomId, index)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                item)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.inventory[0].itemName", is("Coffee Table")))
                                .andExpect(jsonPath("$.inventory[0].link",
                                                is("https://www.laura-james.co.uk/products/imogen-110cm-coffee-table-chalked-mangowood")));

                verify(roomService).editItem(roomId, item, index);
        }

        @Test
        @DisplayName("DeleteItem: Item is deleted")
        void testDeleteItem() throws Exception {
                // Given
                Long id = 2L;
                int index = 1;
                doNothing().when(roomService).deleteItem(id, index);

                // When/Then
                mockMvc.perform(delete("/api/rooms/{id}/inventory/{index}", id, index)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(roomService).deleteItem(id, index);
        }

}
