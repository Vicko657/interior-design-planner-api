package com.interiordesignplanner.project;

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

import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest Controller for managing projects
 * 
 * API endpoints to complete CRUD operations.
 */
@Validated
@RestController
@RequestMapping("/api")
public class ProjectController {

        // Project Service layer
        @Autowired
        public ProjectService projectService;

        /**
         * GET: Returns all Projects
         * 
         * @return all projects entities on the system
         * @response 200 if all projects are found
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Retrieves all of the client's projects", description = "Retrieves all the project information, including the which clients project it is, name, the budget, project status, start date, deadline and meeting links")
        @ApiResponse(responseCode = "200", description = "All projects are found")
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "/admin/projects", produces = "application/json")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Page<ProjectDTO>> getAllProjects(@Valid @RequestParam(required = false) String filter,
                        Pageable pageable) {
                return ResponseEntity.ok(projectService.getAllProjects(filter, pageable));
        }

        /**
         * GET: Returns all Projects
         * 
         * @return all project entities on the system
         * @response 200 if all projects are found
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Retrieves all projects", description = "Retrieves all the projects details, including their status, duedate, client and other details")
        @ApiResponse(responseCode = "200", description = "All projects are found")
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "/projects", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public Page<ProjectSummaryDTO> getProjects(
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails,
                        Pageable pageable) {
                return projectService.getProjectsByDesigner(applicationUserDetails.getUsername(), pageable);
        }

        /**
         * GET: Returns Project with Id
         * 
         * @param id the project's unique identifier
         * @return project's entity
         * @response 200 if project was successfully found
         * @response 404 Not found is the client doesnt exist
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Finds project by ID", description = "Returns one project, including their name, the budget, project status, start date, deadline and meeting links")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Project with id was found"),
                        @ApiResponse(responseCode = "404", description = "Project doesn't exist") })
        @GetMapping(value = "admin/projects/{projectId}", produces = "application/json")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long projectId) {

                ProjectDTO project = projectService.getProjectById(projectId);
                return ResponseEntity.ok(project);

        }

        /**
         * POST: Adds a new Project to a Client
         * 
         * @param clientId the client's unique identifier
         * @param project  the project's object to be created
         * @return saved project with generated unique identifier
         * @response 201 if project was successfully created
         * @response 400 bad request is input data is invalid
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Adds a project to a client", description = "Creates a new project for the client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Project was created"),
                        @ApiResponse(responseCode = "404", description = "Project columns have not been filled") })
        @PostMapping("projects/{clientId}")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO,
                        @PathVariable("clientId") Long clientId,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                ProjectDTO savedProject = projectService.createProject(projectCreateDTO, clientId,
                                applicationUserDetails.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);

        }

        /**
         * PUT: Updates existing project details
         * 
         * @param projectId     the project's unique identifier
         * @param updateProject the project's object to be updated
         * @return updated project entity
         * @response 201 if project was successfully updated
         * @response 404 not found is the project doesn't exist
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Update project", description = "Updates the projects information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Project with id was updated"),
                        @ApiResponse(responseCode = "404", description = "Project doesn't exist") })
        @PutMapping(value = "projects/{projectId}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<ProjectDTO> updateProject(@PathVariable("projectId") Long projectId,
                        @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                ProjectDTO updatedProject = projectService.updateProject(projectId, projectUpdateDTO,
                                applicationUserDetails.getUsername());
                return ResponseEntity.ok(updatedProject);

        }

        /**
         * PATCH: Reassigns project with a different client
         * 
         * @param clientId  the client's unique identifier
         * @param projectId the project's unique identifier
         * @return a new many to one relationship with a client
         * @response 200 if project is reassigned
         * @response 404 if projectId or ClientId is not found
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Reassigns project to a different client", description = "Updates to a different client for the project")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Project with id is reassigned"),
                        @ApiResponse(responseCode = "404", description = "Project or Client doesn't exist") })
        @PatchMapping(value = "projects/{projectId}/clients/{clientId}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<ProjectDTO> reassignClient(@PathVariable("projectId") Long projectId,
                        @PathVariable("clientId") Long clientId,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                ProjectDTO reassignedProject = projectService.reassignClient(clientId, projectId,
                                applicationUserDetails.getUsername());
                return ResponseEntity.ok(reassignedProject);

        }

        /**
         * GET: Returns all Projects with the same status
         * 
         * @return all projects with the same specific status
         * @response 200 if all projects with the same status are returned
         * @response 404 if Status is not found
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Finds project by status", description = "Returns the projects that have the same status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Project status is found"),
                        @ApiResponse(responseCode = "404", description = "Project status doesn't exist") })
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "projects/status", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public Page<ProjectDTO> getProjectsByStatus(@RequestParam String status, Pageable pageable) {

                return projectService.getProjectsByStatus(ProjectStatus.valueOf(status.toUpperCase()), pageable);

        }

        /**
         * GET: Returns all Projects in order of due date
         * 
         * @return all projects in a ascending order of due date
         * @response 200 if all project's are found
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Project deadlines", description = "Returns the projects in order of deadline")
        @ApiResponse(responseCode = "200", description = "All projects are found")
        @ResponseStatus(HttpStatus.OK)
        @GetMapping(value = "projects/deadlines/all", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public Page<Deadline> sortsProjectsByDueDate(Pageable pageable) {
                return projectService.sortsProjectsByDueDate(pageable);
        }

        /**
         * DELETE: Deletes existing Project
         * 
         * @param projectId the project's unique identifier
         * @return deleted project entity off the system
         * @response 204 if project was successfully deleted
         * @response 404 not found is the project doesn't exist
         */
        @Tag(name = "projects", description = "Client's Project directory")
        @Operation(summary = "Deletes project", description = "Deletes the project and its information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Project with id was deleted"),
                        @ApiResponse(responseCode = "404", description = "Project doesn't exist") })
        @DeleteMapping(value = "projects/{id}", produces = "application/json")
        @PreAuthorize("hasRole('DESIGNER')")
        public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                        @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

                projectService.deleteProject(id, applicationUserDetails.getUsername());
                return ResponseEntity.noContent().build();

        }

}
