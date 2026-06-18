package com.interiordesignplanner.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Rest Controller for managing Dashboard view
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Dashboard", description = "Designer's dashboard")
@RestController
@RequestMapping("/api/designer")
public class DashboardController {

    // Dashbaord Service layer
    @Autowired
    public DashboardService dashboardService;

    /**
     * GET: Returns auth designer's dashboard
     * 
     * @param username the designer's username
     * @return designer dashboard
     * @response 200 if dashboard was successfully returned
     * @response 404 access denied
     */
    @Operation(summary = "Get dashboard", description = "Returns the auth designer's dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return's dashboard"),
            @ApiResponse(responseCode = "403", description = "Access Denied") })
    @PreAuthorize("hasRole('DESIGNER')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {
        DashboardDTO dashboardDTO = dashboardService.getDashboard(applicationUserDetails.getUsername());
        return ResponseEntity.ok(dashboardDTO);
    }
}
