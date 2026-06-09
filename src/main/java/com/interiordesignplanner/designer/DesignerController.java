package com.interiordesignplanner.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * Rest Controller for managing designer details
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Designer", description = "Authenticated designer's profile")
@Validated
@RestController
@RequestMapping("/api/designer")
public class DesignerController {

    // Designer Service layer
    @Autowired
    public DesignerService designerService;

    /**
     * GET: Returns auth designer's profile
     * 
     * @param username the designer's username
     * @return designer entity
     * @response 200 if designer was successfully returned
     * @response 404 access denied
     */
    @Operation(summary = "Get profile", description = "Returns the auth designer's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Designer was updated"),
            @ApiResponse(responseCode = "403", description = "Access Denied") })
    @GetMapping(value = "/profile", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<DesignerProfileDTO> getProfile(
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {
        DesignerProfileDTO designerProfileDTO = designerService.getProfile(applicationUserDetails.getUsername());
        return ResponseEntity.ok(designerProfileDTO);
    }

    /**
     * PUT: Updates auth designer's profile
     * 
     * @param username                 the designer's username
     * @param designerProfileUpdateDTO the designer's object to be updated
     * @return updated designer entity
     * @response 200 if designer was successfully updated
     * @response 404 not found is the client doesnt exist
     */
    @Operation(summary = "Update profile", description = "Updates the auth designer's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Designer was updated"),
            @ApiResponse(responseCode = "403", description = "Access Denied") })
    @PutMapping(value = "/profile", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<DesignerProfileDTO> updateProfile(
            @Valid @RequestBody DesignerProfileUpdateDTO designerProfileUpdateDTO,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {
        DesignerProfileDTO updatedProfile = designerService.updateProfile(
                designerProfileUpdateDTO,
                applicationUserDetails.getUsername());
        return ResponseEntity.ok(updatedProfile);

    }

}
