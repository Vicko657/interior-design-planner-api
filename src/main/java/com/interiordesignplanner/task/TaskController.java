package com.interiordesignplanner.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.task.Task;
import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest Controller for managing tasks.
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Tasks", description = "Room's tasks")
@Validated
@RestController
@RequestMapping("/api")
public class TaskController {

    // Task Service layer
    @Autowired
    public TaskService taskService;

    /**
     * PATCH: Adds new Task to Room
     * 
     * @param roomId the project's unique identifier
     * @param room   the room's object to be created
     * @return saved room for project with generated unique identifier
     * @response 201 if the room was successfully created
     * @response 404 bad request is input data is invalid
     */
    @Operation(summary = "Adds task", description = "Adds a new task to the room's checklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was added"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @PatchMapping(value = "/rooms/{roomId}/task", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<RoomDTO> addTask(@Valid @RequestBody Task task,
            @PathVariable("roomId") Long roomId,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        RoomDTO savedTask = taskService.addTask(roomId, task, applicationUserDetails.getUsername());
        return ResponseEntity.ok(savedTask);

    }

    /**
     * PATCH: Edit Task from Checklist
     * 
     * @param roomId   the project's unique identifier
     * @param editTask the room's task updated
     * @param index    the target task to update
     * @return saved room for project with generated unique identifier
     * @response 200 if the room was successfully updated
     * @response 404 bad request is input data is invalid
     */
    @Operation(summary = "Edit task", description = "Edit task to the room's checklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was added"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @PatchMapping(value = "/rooms/{roomId}/task/{index}", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<RoomDTO> editTask(@Valid @RequestBody Task editTask,
            @PathVariable("roomId") Long roomId, @PathVariable("index") int index,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        RoomDTO updatedTask = taskService.editTask(roomId, editTask, index,
                applicationUserDetails.getUsername());
        return ResponseEntity.ok(updatedTask);

    }

    /**
     * DELETE: Removes task for Room
     * 
     * @param roomId the room's unique identifier
     * @param index  the specific task key
     * @return removed task off the checklist
     * @response 204 if task was successfully deleted
     * @response 404 not found is the room doesn't exist
     */
    @Operation(summary = "Deletes task", description = "Deletes specific task for room and its details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task with id was deleted"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @DeleteMapping(value = "/rooms/{roomId}/task/{index}", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<Void> deleteTask(@PathVariable("roomId") Long roomId, @PathVariable int index,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        taskService.deleteTask(roomId, index, applicationUserDetails.getUsername());
        return ResponseEntity.noContent().build();

    }

}
