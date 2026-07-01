package com.interiordesignplanner.projectsummary;

import java.math.BigDecimal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.mapper.ProjectMapper;
import com.interiordesignplanner.project.Project;
import com.interiordesignplanner.project.ProjectService;
import com.interiordesignplanner.room.RoomService;

@Service
public class ProjectSummaryService {

    // Client Service layer
    private final ClientService clientService;

    // Project Mapper
    private final ProjectMapper projectMapper;

    // Project Service layer
    private final ProjectService projectService;

    // Room Service layer
    private final RoomService roomService;

    public ProjectSummaryService(ClientService clientService,
            ProjectMapper projectMapper, RoomService roomService, ProjectService projectService) {
        this.projectService = projectService;
        this.clientService = clientService;
        this.projectMapper = projectMapper;
        this.roomService = roomService;
    }

    /**
     * Returns the project details.
     * 
     * @param username  retrieves the projects assigned designer
     * @param projectId retrieves the project unique identifier
     * @return specific project summary
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public ProjectSummaryDTO getProjectSummary(String username, Long projectId) {

        Project project = projectService.findProject(projectId);

        Client existingClient = clientService.findClient(project.getClient().getId());

        projectService.findClientByDesigner(existingClient, username);

        ProjectSummaryDTO projectSummary = projectMapper.toSummaryDto(project);

        if (project.getRoom() == null || project.getRoom().getId() == null) {

            projectSummary.setHeight(0.0);
            projectSummary.setLength(0.0);
            projectSummary.setWidth(0.0);
            projectSummary.setRoom(null);
            projectSummary.setUnit(" ");
        }

        if (project.getRoom() == null) {
            projectSummary.setRemainingBudget(project.getBudget());
        } else {
            calculateRemainingBudget(projectSummary, project.getBudget(), project.getRoom().getId());
        }

        return projectSummary;
    }

    /**
     * Calculates sum of the remaining budget.
     * 
     * @param projectSummary sets fields in the ProjectSummaryDTO
     * @param budget         retrieves the projects budget
     * @param roomId         retrieves the room unique identifier
     * @return the calculation of the remaining budget
     */
    private void calculateRemainingBudget(ProjectSummaryDTO projectSummary, BigDecimal budget, Long roomId) {

        BigDecimal totalInventory = roomService.calculateTotalInventory(roomId);
        BigDecimal remainingBudget = budget.subtract(totalInventory);

        projectSummary.setRemainingBudget(remainingBudget);

    }

}
