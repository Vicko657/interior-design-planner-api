package com.interiordesignplanner.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.interiordesignplanner.task.Task;

@Service
public class TaskService {

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final AuthenticationService authenticationService;
    private final DesignerService designerService;

    // Constructor
    public TaskService(RoomRepository roomRepository, RoomService roomService, RoomMapper roomMapper,
            AuthenticationService authenticationService, DesignerService designerService) {
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.roomMapper = roomMapper;
        this.authenticationService = authenticationService;
        this.designerService = designerService;
    }

    /**
     * Returns the designer's tasks.
     * 
     * 
     * 
     * @param username retrieves the logged in user
     * @param pageable pagination info
     * @return the list of tasks
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<TaskDTO> getTasks(String username, Pageable pageable) {

        User user = authenticationService.findUser(username);

        Designer designer = designerService.findDesigner(user.getId());

        return roomRepository.findTasks(designer.getId(), pageable);
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
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO addTask(Long roomId, Task task, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

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
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public RoomDTO editTask(Long roomId, Task updateTask, int index, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

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
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteTask(Long roomId, int index, String username) {

        Room existingRoom = roomService.findRoom(roomId);

        roomService.findRoomByDesigner(existingRoom, username);

        existingRoom.getChecklist().remove(index);

        roomMapper.toDto(roomRepository.save(existingRoom));
    }

}
