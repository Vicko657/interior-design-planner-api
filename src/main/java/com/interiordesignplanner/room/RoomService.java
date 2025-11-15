package com.interiordesignplanner.room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.exceptions.RoomNotFoundException;
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

    // Constructor
    public RoomService(RoomRepository roomRepository, ProjectService projectService) {
        this.roomRepository = roomRepository;
        this.projectService = projectService;

    }

    /**
     * Returns all rooms created for projects on the system.
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
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
    public List<Room> getRoomsByType(String type) {

        RoomType typeValues = null;

        for (RoomType type1 : RoomType.values()) {
            if (type1.name().equalsIgnoreCase(type.trim())) {
                typeValues = type1;
                break;
            }
        }

        if (typeValues != null) {
            return roomRepository.findRoomsByType(typeValues);
        } else {
            throw new RoomNotFoundException("roomType", type);
        }

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
    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("roomId", id));
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
    public Room addRoom(Room room, Long projectId) throws IllegalArgumentException {
        if (room == null && projectId == null) {
            throw new IllegalArgumentException("Room must not be null");
        }

        Project project = projectService.getProject(projectId);
        project.setRoom(room);
        room.setProject(project);
        projectService.saveProjectEntity(project);
        return room;

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
    public Room updateRoom(Long id, Room room) {
        Room existingRoomId = getRoom(id);
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException("roomId", id);
        } else {
            existingRoomId.setType(room.getType());
            existingRoomId.setHeight(room.getHeight());
            existingRoomId.setLength(room.getLength());
            existingRoomId.setWidth(room.getWidth());
            existingRoomId.setUnit(room.getUnit());
            existingRoomId.setChecklist(room.getChecklist());
            existingRoomId.setChanges(room.getChanges());
        }
        return roomRepository.save(existingRoomId);
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

        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException("roomId", id);
        }
        roomRepository.deleteById(id);

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
    public Room reassignProject(Long projectId, Long roomId) {

        Room existingRoomId = getRoom(roomId);
        Project project = projectService.getProject(projectId);
        if (existingRoomId == null) {
            throw new RoomNotFoundException("roomId", roomId);
        } else if (projectId == null) {
            throw new ProjectNotFoundException("projectId", projectId);
        } else {
            project.setRoom(existingRoomId);
            existingRoomId.setProject(project);
        }
        return roomRepository.save(existingRoomId);
    }

}
