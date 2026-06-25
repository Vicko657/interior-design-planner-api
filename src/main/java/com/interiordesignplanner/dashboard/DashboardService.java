package com.interiordesignplanner.dashboard;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.dashboard.DashboardDTO.CurrentBudget;
import com.interiordesignplanner.dashboard.DashboardDTO.ProjectProgress;
import com.interiordesignplanner.dashboard.DashboardDTO.RecentTasks;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.exceptions.UserNotFoundException;
import com.interiordesignplanner.project.ProjectRepository;
import com.interiordesignplanner.project.ProjectStatus;
import com.interiordesignplanner.room.RoomRepository;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final AuthenticationService authenticationService;
    private final DesignerService designerService;
    private final RoomRepository roomRepository;

    public DashboardService(ProjectRepository projectRepository, AuthenticationService authenticationService,
            DesignerService designerService, RoomRepository roomRepository) {

        this.projectRepository = projectRepository;
        this.authenticationService = authenticationService;
        this.designerService = designerService;
        this.roomRepository = roomRepository;

    }

    /**
     * Return the Designer's dashboard
     * 
     * 
     * @param username retrieves the current auth user
     * @throws UserNotFoundException if the user is not found
     * @return the designer's dashboard
     */
    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(String username) {

        User user = authenticationService.findUser(username);
        Designer designer = designerService.findDesigner(user.getId());
        Long designerId = designer.getId();

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setName(user.getFirstName() + " " + user.getLastName());
        projectCalculations(dashboard, designerId);
        projectProgress(dashboard, designerId);
        recentTasks(dashboard, designerId);
        calculateBudget(dashboard, designerId);

        return dashboard;
    }

    /**
     * Calculates size of active, completed and total projects
     * 
     * @param dashboard  retrieves the DashboardDTO
     * @param designerId retrieves the current auth designer's id
     * @return the totals of the designer's active, completed and all projects
     */
    private void projectCalculations(DashboardDTO dashboard, Long designerId) {

        Integer activeNo;
        Integer totalNo;
        Integer completedNo;

        // Active projects
        List<Object[]> active = projectRepository.findTotalProjectStatus(designerId, ProjectStatus.ACTIVE);
        // Total projects
        List<Object[]> total = projectRepository.findTotalProjects(designerId);
        // Completed projects
        List<Object[]> completed = projectRepository.findTotalProjectStatus(designerId, ProjectStatus.COMPLETED);

        if (active.size() != 0 || !active.isEmpty()) {
            activeNo = active.size();
        } else {
            activeNo = 0;
        }

        if (total.size() != 0 || !total.isEmpty()) {
            totalNo = total.size();
        } else {
            totalNo = 0;
        }

        if (completed.size() != 0 || !completed.isEmpty()) {
            completedNo = completed.size();
        } else {
            completedNo = 0;
        }

        dashboard.setActiveProjects(activeNo);
        dashboard.setCompletedProjects(completedNo);
        dashboard.setTotalProjects(totalNo);
    }

    /**
     * Returns designer's unfinished projects by due date
     * 
     * @param dashboard  retrieves the DashboardDTO
     * @param designerId retrieves the current auth designer's id
     * @return the list of projects due
     */
    private void projectProgress(DashboardDTO dashboard, Long designerId) {

        List<ProjectProgress> progressList = projectRepository.findProjectProgress(designerId, PageRequest.of(0, 3));

        dashboard.setProjectProgress(progressList);
    }

    /**
     * Return the designer's tasks for projects
     * 
     * @param dashboard  retrieves the DashboardDTO
     * @param designerId retrieves the current auth designer's id
     * @return the designer's tasks
     */
    private void recentTasks(DashboardDTO dashboard, Long designerId) {

        List<RecentTasks> taskList = roomRepository.findRecentTasks(designerId, PageRequest.of(0, 4));

        dashboard.setRecentTasks(taskList);
    }

    /**
     * Return the designers clients current budgets
     * 
     * @param dashboard  retrieves the DashboardDTO
     * @param designerId retrieves the current auth designer's id
     * @return the clients current budget
     */
    private void calculateBudget(DashboardDTO dashboard, Long designerId) {

        List<CurrentBudget> budgetList = projectRepository.findClientTotalBudget(designerId, PageRequest.of(0, 4));

        dashboard.setBudget(budgetList);
    }

}
