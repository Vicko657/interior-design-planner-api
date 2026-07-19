package com.interiordesignplanner.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.interiordesignplanner.room.RoomDTO;
import com.interiordesignplanner.security.ApplicationUserDetails;
import com.interiordesignplanner.task.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest Controller for managing inventories.
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Inventory", description = "Room's inventory items")
@Validated
@RestController
@RequestMapping("/api")
public class InventoryController {

    // Inventory Service layer
    @Autowired
    public InventoryService inventoryService;

    /**
     * PATCH: Adds new item to Inventory
     * 
     * @param roomId the project's unique identifier
     * @param item   the new item is added to the inventory list
     * @return saved room for project with generated unique identifier
     * @response 200 if the room was successfully created
     * @response 404 bad request is input data is invalid
     */
    @Operation(summary = "Adds item", description = "Adds a new item to the room's inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was added"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @PatchMapping(value = "/rooms/{roomId}/inventory", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<RoomDTO> addItem(@Valid @RequestBody Item item,
            @PathVariable("roomId") Long roomId,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        RoomDTO savedTask = inventoryService.addItem(roomId, item, applicationUserDetails.getUsername());
        return ResponseEntity.ok(savedTask);

    }

    /**
     * PATCH: Edit Item from Inventory
     * 
     * @param roomId   the project's unique identifier
     * @param editItem the room's item updated
     * @param index    the target item to update
     * @return saved room for project with generated unique identifier
     * @response 200 if the room was successfully updated
     * @response 404 bad request is input data is invalid
     */
    @Operation(summary = "Edit item", description = "Edit item from inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was added"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @PatchMapping(value = "/rooms/{roomId}/inventory/{index}", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<RoomDTO> editTask(@Valid @RequestBody Item editItem,
            @PathVariable("roomId") Long roomId, @PathVariable("index") int index,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        RoomDTO updatedItem = inventoryService.editItem(roomId, editItem, index,
                applicationUserDetails.getUsername());
        return ResponseEntity.ok(updatedItem);

    }

    /**
     * DELETE: Removes item from Inventory
     * 
     * @param roomId the room's unique identifier
     * @param index  the specific task key
     * @return removed item off the inventory
     * @response 204 if task was successfully deleted
     * @response 404 not found is the room doesn't exist
     */
    @Operation(summary = "Deletes item", description = "Deletes specific item from inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item with id was deleted"),
            @ApiResponse(responseCode = "404", description = "Room doesn't exist") })
    @DeleteMapping(value = "/rooms/{roomId}/inventory/{index}", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<Void> deleteItem(@PathVariable("roomId") Long roomId, @PathVariable int index,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        inventoryService.deleteItem(roomId, index, applicationUserDetails.getUsername());
        return ResponseEntity.noContent().build();

    }
}
