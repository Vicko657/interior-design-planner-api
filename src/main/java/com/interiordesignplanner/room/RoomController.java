package com.interiordesignplanner.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.interiordesignplanner.inventory.Item;
import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest Controller for managing rooms.
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Rooms", description = "Project's room specification")
@Validated
@RestController
@RequestMapping("/api")
public class RoomController {

        // Room Service layer
        @Autowired
        public RoomService roomService;

        /**
         * GET: Returns Room with Id
         * 
         * @param id the room's unique identifier
         * @return room's entity
         * @response 200 if room was successfully found
         * @response 404 Not found is the room doesnt exist
         */
        @Operation(summary = "Finds room by ID", description = "Returns one room, including their roomType, roomSize, checkList of tasks, changes to the room")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Room with id was found"),
                        @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
        @GetMapping(value = "/rooms/{id}", produces = "application/json")
        @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
        public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {

                RoomDTO room = roomService.getRoomById(id);
                return ResponseEntity.ok(room);

        }

        /**
         * GET: Returns all Rooms
         * 
         * @return all room entities on the system
         * @response 200 if all rooms are found
         */
        @Operation(summary = "Retrieves all of the rooms", description = "Returns all the room specification, including the client and project it is linked to, roomType, roomSize, checkList of tasks, changes to the room")
        @ApiResponse(responseCode = "200", description = "All rooms are found")
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "/admin/rooms", produces = "application/json")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Page<RoomDTO>> getAllRooms(@Valid @RequestParam(required = false) String filter,
                        Pageable pageable) {
                return ResponseEntity.ok(roomService.getAllRooms(filter, pageable));
        }

        /**
         * POST: Adds a new Project to a Client
         * 
         * @param projectId the project's unique identifier
         * @param room      the room's object to be created
         * @return saved room for project with generated unique identifier
         * @response 201 if the room was successfully created
         * @response 404 bad request is input data is invalid
         */
        @Operation(summary = "Adds a room to the project", description = "Creates a room with specifications for the project")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Room was created"),
                        @ApiResponse(responseCode = "404", description = "Room columns have not been filled") })
        @PostMapping(value = "/rooms/{projectId}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<RoomDTO> addRoom(@Valid @RequestBody RoomCreateDTO roomCreateDTO,
                        @PathVariable("projectId") Long projectId,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                RoomDTO savedRoom = roomService.addRoom(roomCreateDTO, projectId, applicationUserDetails.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);

        }

        /**
         * PUT: Updates existing room details
         * 
         * @param roomId     the room's unique identifier
         * @param updateRoom the room's object to be updated
         * @return updated room entity
         * @response 201 if room was successfully updated
         * @response 404 not found is the room doesn't exist
         */
        @Operation(summary = "Updates room", description = "Updates the room's specification")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Room with id was updated"),
                        @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
        @PutMapping(value = "/rooms/{roomId}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<RoomDTO> updateRoom(@PathVariable("roomId") Long roomId,
                        @Valid @RequestBody RoomUpdateDTO roomUpdateDTO,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                RoomDTO updatedRoom = roomService.updateRoom(roomId, roomUpdateDTO,
                                applicationUserDetails.getUsername());
                return ResponseEntity.ok(updatedRoom);

        }

        /**
         * PATCH: Reassigns room with a different project
         * 
         * @param projectId the project's unique identifier
         * @param roomId    the room's unique identifier
         * @return a new one to one relationship with
         * @response 200 if room is reassigned to a project
         * @response 404 if projectId or roomId is not found
         */
        @Operation(summary = "Reassigns room to a different project", description = "Updates to a different project for the room")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Room with id is reassigned"),
                        @ApiResponse(responseCode = "404", description = "Room or Project doesn't exist") })
        @PatchMapping(value = "/rooms/{roomId}/projects/{projectId}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<RoomDTO> reassignProject(@PathVariable("roomId") Long roomId,
                        @PathVariable("projectId") Long projectId,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                RoomDTO reassignedRoom = roomService.reassignProject(projectId, roomId,
                                applicationUserDetails.getUsername());
                return ResponseEntity.ok(reassignedRoom);

        }

        /**
         * GET: Returns all Rooms with the same type
         * 
         * @return all rooms with the same specific type
         * @response 200 if all room's with the same type are returned
         * @response 404 if room type is not found
         */
        @Operation(summary = "Finds room by type", description = "Returns the same type of rooms")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Room type is found"),
                        @ApiResponse(responseCode = "404", description = "Room type doesn't exist") })
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "/rooms/type", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public Page<RoomDTO> getType(@RequestParam String type, Pageable pageable,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                return roomService.getRoomsByType(RoomType.valueOf(type.toUpperCase()), pageable,
                                applicationUserDetails.getUsername());

        }

        /**
         * DELETE: Deletes existing Room
         * 
         * @param roomId the room's unique identifier
         * @return deleted room entity off the system
         * @response 204 if room was successfully deleted
         * @response 404 not found is the room doesn't exist
         */
        @Operation(summary = "Deletes room", description = "Deletes the room and its specifications")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Room with id was deleted"),
                        @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
        @DeleteMapping(value = "/rooms/{id}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                roomService.deleteRoom(id, applicationUserDetails.getUsername());
                return ResponseEntity.noContent().build();

        }

}
