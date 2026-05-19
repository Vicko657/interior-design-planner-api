package com.interiordesignplanner.room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
import com.interiordesignplanner.mapper.RoomMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectService;

import io.github.perplexhub.rsql.RSQLJPASupport;

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
    private final ProjectService projectService;

    // Room CRUD Interface
    private final RoomRepository roomRepository;

    // Room Mapper
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
    @PreAuthorize("hasRole('ADMIN')")
    public Page<RoomDTO> getAllRooms(String filter, Pageable pageable) {

        Page<Room> rooms;

        if (filter != null) {
            Specification<Room> specfication = RSQLJPASupport.toSpecification(filter);
            rooms = roomRepository.findAll(specfication, pageable);
        } else {
            rooms = roomRepository.findAll(pageable);
        }

        return rooms.map(room -> {
            RoomDTO roomDTO = roomMapper.toDto(room);
            return roomDTO;
        });
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
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<RoomDTO> getRoomsByType(RoomType type, Pageable pageable, String username) {

        if (type == null) {
            throw new RoomNotFoundException("type", type);
        }

        return roomRepository.findRoomsByType(
                type, pageable)
                .map(room -> {
                    RoomDTO roomDTO = roomMapper.toDto(room);
                    return roomDTO;
                });

    }

    /**
     * Returns a room using their roomId.
     * 
     * <p>
     * Used to access a specific room within a project for updates
     * or design changes.
     * </p>
     * 
     * @param id room's unique identifier
     * @throws RoomNotFoundException if the room is not found
     */
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO addRoom(RoomCreateDTO roomCreateDTO, Long projectId,
            String username) {

        if (roomCreateDTO == null && projectId == null) {
            throw new IllegalArgumentException("Room must not be null");
        }

        Project project = projectService.findProject(projectId);

        if (project.getClient().getDesigner().getUser().getUsername() != username) {
            throw new AccessDeniedException("User does not have authorization");
        }
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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO updateRoom(Long id, RoomUpdateDTO roomUpdateDTO, String username) {

        Room existingRoom = findRoom(id);
        findRoomByDesigner(existingRoom, username);
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
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteRoom(Long id, String username) {

        Room room = findRoom(id);
        findRoomByDesigner(room, username);
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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO reassignProject(Long projectId, Long roomId, String username) {

        Room existingRoom = findRoom(roomId);
        Project project = projectService.findProject(projectId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO addTask(Long roomId, Task task, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO editTask(Long roomId, Task updateTask, int index, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteTask(Long roomId, int index, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO addItem(Long roomId, Item item, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO editItem(Long roomId, Item updateItem, int index, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteItem(Long roomId, int index, String username) {

        Room existingRoom = findRoom(roomId);

        findRoomByDesigner(existingRoom, username);

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

    public void findRoomByDesigner(Room existingRoom, String username) {

        Project existingProject = projectService.findProject(existingRoom.getProject().getId());

        if (existingProject.getClient().getDesigner().getUser().getUsername() != username) {
            throw new AccessDeniedException("User does not have authorization");
        }

    }

}
