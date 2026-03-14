package com.interiordesignplanner.room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectService;

/**
 * Manages business logic related to rooms within a project.
 * 
 * <p>
 * Responsibilities include creating, updating, retrieving and deleting room
 * specifications, such as dimensions, type, maintaining checklists and change
 * logs, and associating rooms with projects and tasks.
 * 
 * Serves as an interface between controllers and the persistence layer.
 * </p>
 */
@Service
public class RoomService {

    // Project Service layer
    @Autowired
    private final ProjectService projectService;

    // Room CRUD Interface
    @Autowired
    private final RoomRepository roomRepository;

    // Room Mapper
    @Autowired
    private final RoomMapper roomMapper;

    // Constructor
    public RoomService(RoomRepository roomRepository, ProjectService projectService, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.projectService = projectService;
        this.roomMapper = roomMapper;

    }

    /**
     * Returns all rooms created for projects on the system.
     */
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> {
                    RoomDTO roomDTO = roomMapper.toDto(room);
                    return roomDTO;
                }).toList();
    }

    /**
     * Gets all rooms with the same room type.
     * 
     * <p>
     * Returns all rooms with the same a room type to narrow down the search and
     * help with auditing
     * 
     * Custom query created in the repository.
     * </p>
     * 
     * @param type room type enum
     * @returns rooms with same type
     * @throws RoomNotFoundException if the room type is not found
     */
    public List<RoomDTO> getRoomsByType(RoomType type) {

        if (type == null) {
            throw new RoomNotFoundException("type", type);
        }

        return roomRepository.findRoomsByType(
                type).stream()
                .map(room -> {
                    RoomDTO roomDTO = roomMapper.toDto(room);
                    return roomDTO;
                }).toList();

    }

    /**
     * Returns a room using their roomId.
     * 
     * <p>
     * Used to access a specific room within a project for updates
     * or design changes.
     * </p>
     * 
     * @param id project's unique identifier
     * @throws RoomNotFoundException if the room is not found
     */
    public RoomDTO getRoomById(Long id) {

        Room room = findRoom(id);
        RoomDTO roomDTO = roomMapper.toDto(room);

        return roomDTO;
    }

    /**
     * One to One relationship with Project happens atomically,
     * so if one fails, neither is persisted.
     * 
     * <p>
     * Add a room to an existing project. The room will be linked
     * to the specified project and assigned a unique identifier.
     * </p>
     * 
     * @param projectId project's unique identifier
     * @param room      room object to be created
     * @throws IllegalArgumentException if room fields are null
     * @return room with a generated unique Id
     */
    @Transactional
    public RoomDTO addRoom(RoomCreateDTO roomCreateDTO, Long projectId) throws IllegalArgumentException {

        if (roomCreateDTO == null && projectId == null) {
            throw new IllegalArgumentException("Room must not be null");
        }

        Project project = projectService.findProject(projectId);
        roomCreateDTO.setProject(project);
        Room room = roomMapper.toEntity(roomCreateDTO);
        project.setRoom(room);
        projectService.saveProjectEntity(project);
        Room saveRoom = roomRepository.save(room);
        return roomMapper.toDto(saveRoom);

    }

    /**
     * Updates a existing room details.
     * 
     * <p>
     * Modifys room details such as dimensions, style or checklist and changes.
     * </p>
     * 
     * @param id   room's unique identifier
     * @param room room object to be created
     * @throws RoomNotFoundException if the room is not found
     * @return updates room
     */
    public RoomDTO updateRoom(Long id, RoomUpdateDTO roomUpdateDTO) {

        Room existingRoom = findRoom(id);
        roomMapper.updateEntity(roomUpdateDTO, existingRoom);
        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Deletes a existing room.
     * 
     * *
     * <p>
     * Use this method when a room is removed from the project scope. Deletion
     * will respect the defined cascade and orphan removal settings.
     * </p>
     *
     * 
     * @param id room's unique identifier
     * @return deletes room details
     * @throws RoomNotFoundException if the room doesnt exist
     */
    public void deleteRoom(Long id) {

        Room room = findRoom(id);
        roomRepository.delete(room);

    }

    /**
     * Reassigns room to a existing project.
     * 
     * <p>
     * Use this method when a room is assigned to the wrong project
     * and needs to be reassigned.
     * It will update the many to one relationship.
     * </p>
     *
     * 
     * @param projectId project's unique identifier
     * @param roomId    room's unique identifier
     * @throws RoomNotFoundException    if the room doesn't exist
     * @throws ProjectNotFoundException if the project doesn't exist
     * @return room is reassigned
     */
    public RoomDTO reassignProject(Long projectId, Long roomId) {

        Room existingRoom = findRoom(roomId);
        Project project = projectService.findProject(projectId);

        if (existingRoom == null || project == null) {
            throw new RoomNotFoundException("roomId", roomId);
        } else {
            project.setRoom(existingRoom);
            existingRoom.setProject(project);
        }

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Adds Task to checklist for the room
     * 
     * 
     * 
     * @param task   the task object to be updated
     * @param roomId retrieves the room object to be updated
     * @throws RoomNotFoundException if the room is not found
     * @return the updated room
     */
    public RoomDTO addTask(Long roomId, Task task) {

        Room existingRoom = findRoom(roomId);

        task.setCompleted(false);

        existingRoom.getChecklist().add(task);

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Updates task to checklist
     * 
     * 
     * 
     * @param index      retrieves the item to be updated
     * @param roomId     retrieves the room object to be updated
     * @param updateTask the updated task
     * @throws RoomNotFoundException if the room is not found
     * @return the updated task is added to the checklist
     */
    public RoomDTO editTask(Long roomId, Task updateTask, int index) {

        Room existingRoom = findRoom(roomId);

        existingRoom.getChecklist().set(index, updateTask);

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Delete Task to checklist for the room
     * 
     * 
     * 
     * @param index  using the task's index to delete
     * @param roomId retrieves the room object to be updated
     * @throws RoomNotFoundException if the room is not found
     * @return the updated room
     */
    public void deleteTask(Long roomId, int index) {

        Room existingRoom = findRoom(roomId);

        existingRoom.getChecklist().remove(index);

        roomMapper.toDto(roomRepository.save(existingRoom));
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
    public RoomDTO addItem(Long roomId, Item item) {

        Room existingRoom = findRoom(roomId);

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
    public RoomDTO editItem(Long roomId, Item updateItem, int index) {

        Room existingRoom = findRoom(roomId);

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
    public void deleteItem(Long roomId, int index) {

        Room existingRoom = findRoom(roomId);

        existingRoom.getInventory().remove(index);

        roomMapper.toDto(roomRepository.save(existingRoom));
    }

    /**
     * Retrieved the Room's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the room object to be deleted
     * @throws RoomNotFoundException if the room is not found
     * @return the room
     */
    public Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("roomId", id));
    }

}
