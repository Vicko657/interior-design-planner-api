package com.interiordesignplanner.inventory;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.room.RoomRepository;
import com.interiordesignplanner.room.RoomService;
import com.interiordesignplanner.room.RoomType;

@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Inventory Service Test Suite")
public class InventoryServiceTest {

    // Mock room repository
    @Mock
    private RoomRepository roomRepository;

    // Project mapper
    @Autowired
    private RoomMapper roomMapper;

    // Mock inventory service
    @InjectMocks
    private InventoryService inventoryService;

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
    private Item item, item2;
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
        inventoryService = new InventoryService(roomRepository, roomService, roomMapper, authenticationService,
                designerService);

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
        room1.setUnit("m");
        inventory1.add(item2);

        inventory2 = new ArrayList<>();

        room2 = new Room();
        room2.setId(2L);
        room2.setProject(project2);
        room2.setType(RoomType.HALLWAY);
        room2.setHeight(5.8);
        room2.setLength(6.2);
        room2.setWidth(2.3);
        room2.setInventory(inventory2);
        room2.setUnit("m");

        inventory2.add(item);

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

        when(roomService.findRoom(1L)).thenReturn(room1);
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new item, saves room
        RoomDTO result = inventoryService.addItem(roomId, newItem, user.getUsername());

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

        when(roomService.findRoom(1L)).thenReturn(room1);
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer the if room exists, adds a new item, saves room
        RoomDTO result = inventoryService.editItem(roomId, item, index, user.getUsername());

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

        when(roomService.findRoom(1L)).thenReturn(room1);
        when(roomRepository.save(room1)).thenReturn(room1);

        // Act: Query the service layer to return the Room with the id and delete the
        // item and save the room
        inventoryService.deleteItem(roomId, index, user.getUsername());

        // Assert: Verifies that the task was deleted, the size of the list is now 0
        assertEquals(room1.getInventory().size(), 0);
        verify(roomRepository, times(1)).save(any(Room.class));

    }

}
