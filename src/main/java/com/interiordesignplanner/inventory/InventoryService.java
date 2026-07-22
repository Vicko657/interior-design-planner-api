package com.interiordesignplanner.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.room.Room;
import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.room.RoomRepository;
import com.interiordesignplanner.room.RoomService;
import com.interiordesignplanner.task.TaskDTO;

import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final AuthenticationService authenticationService;
    private final DesignerService designerService;

    // Constructor
    public InventoryService(RoomRepository roomRepository, RoomService roomService, RoomMapper roomMapper,
            AuthenticationService authenticationService, DesignerService designerService) {
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.roomMapper = roomMapper;
        this.authenticationService = authenticationService;
        this.designerService = designerService;
    }

    /**
     * Returns the designer's project items.
     * 
     * 
     * 
     * @param username retrieves the logged in user
     * @param pageable pagination info
     * @return the list of items
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<InventoryDTO> getInventories(String username, Pageable pageable) {

        User user = authenticationService.findUser(username);

        Designer designer = designerService.findDesigner(user.getId());

        return roomRepository.findInventory(designer.getId(), pageable);
    }

    /**
     * Adds item to inventory
     * 
     * 
     * 
     * @param item   the item is added to the shopping list
     * @param roomId retrieves the room object to be updated
     * @throws RoomNotFoundException if the room is not found
     * @return the updated room, with a new item on the list
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO addItem(Long roomId, Item item, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

        item.setOrdered(false);

        existingRoom.getInventory().add(item);

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Updates item to inventory
     * 
     * 
     * 
     * 
     * @param index      retrieves the item to be updated
     * @param roomId     retrieves the room object to be updated
     * @param updateItem the updated item
     * @throws RoomNotFoundException if the room is not found
     * @return the updated item is added to the inventory
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO editItem(Long roomId, Item updateItem, int index, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

        existingRoom.getInventory().set(index, updateItem);

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Deletes Item from inventory
     * 
     * 
     * 
     * @param index  using the task's index to delete
     * @param roomId retrieves the room object to be updated
     * @throws RoomNotFoundException if the room is not found
     * @return the updated room
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteItem(Long roomId, int index, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

        existingRoom.getInventory().remove(index);

        roomMapper.toDto(roomRepository.save(existingRoom));
    }

}
