package com.interiordesignplanner.projectsummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Rest Controller for managing Project view
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "ProjectSummary", description = "Project's details view")
@RestController
@RequestMapping("/api/projects")
public class ProjectSummaryController {

    // Project Summary Service layer
    @Autowired
    public ProjectSummaryService projectSummaryService;

    /**
     * GET: Returns Project Summary
     * 
     * @return specific project details
     * @response 200 if project is found
     */
    @Operation(summary = "Returns project summary", description = "Returns the projects details including room details")
    @ApiResponse(responseCode = "200", description = "All projects are found")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{projectId}/summary", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ProjectSummaryDTO getProjectSummary(
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails,
            @PathVariable Long projectId) {
        return projectSummaryService.getProjectSummary(applicationUserDetails.getUsername(), projectId);
    }

}
