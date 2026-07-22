package com.interiordesignplanner.project;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.authentication.AuthenticationService;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.client.ClientService;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerService;
import com.interiordesignplanner.exceptions.ProjectNotFoundException;
import com.interiordesignplanner.mapper.ProjectMapper;

import io.github.perplexhub.rsql.RSQLJPASupport;

/**
 * Project service class handles business logic and operations to help manage
 * the projects.
 * 
 * <p>
 * Responsibilities include creating, updating, retrieving, and deleting project
 * records, enforcing project status (PLANNING, ACTIVE, COMPLETED) to help track
 * progress,
 * managing budgets and deadlines and coordinating relationships with rooms.
 * 
 * Serves as an interface between controllers and the persistence layer.
 * </p>
 */
@Service
public class ProjectService {

    // Project CRUD Interface
    private final ProjectRepository projectRepository;

    // Client Service layer
    private final ClientService clientService;

    // User Service
    private final AuthenticationService authenticationService;

    // Designer Service
    private final DesignerService designerService;

    // Project Mapper
    private final ProjectMapper projectMapper;

    // Constructor
    public ProjectService(ProjectRepository projectRepository, ClientService clientService,
            AuthenticationService authenticationService, DesignerService designerService,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.clientService = clientService;
        this.projectMapper = projectMapper;
        this.authenticationService = authenticationService;
        this.designerService = designerService;
    }

    /**
     * Returns all projects on the system and their room.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProjectDTO> getAllProjects(String filter, Pageable pageable) {

        Page<Project> projects;

        if (filter != null) {
            Specification<Project> specfication = RSQLJPASupport.toSpecification(filter);
            projects = projectRepository.findAll(specfication, pageable);
        } else {
            projects = projectRepository.findAll(pageable);
        }

        return projects.map(project -> {
            ProjectDTO projectDTO = projectMapper.toDto(project);
            return projectDTO;
        });
    }

    /**
     * Returns the designer's project details.
     * 
     * @param username retrieves the logged in user
     * @param filter   the query for search
     * @param pageable pagination info
     * @throws UsernameNotFoundException if the user is not found
     * @throws DesignerNotFoundException if the designer is not found
     * @return logged in designer's paginated list of projects
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<ProjectDTO> getProjectsByDesigner(String username, Pageable pageable, String filter) {

        User user = authenticationService.findUser(username);
        Designer designer = designerService.findDesigner(user.getId());

        Specification<Project> spec = buildSpecification(filter, designer.getId());

        Page<Project> projects = projectRepository.findAll(spec, pageable);

        return projects.map(project -> {
            ProjectDTO projectDTO = projectMapper.toDto(project);
            return projectDTO;
        });
    }

    /**
     * Builds filtered project specification
     * 
     * @param designerId retrieves the projects assigned designer
     * @param filter     the query for search
     * @return project specification
     */
    private Specification<Project> buildSpecification(String filter, Long designerId) {

        Specification<Project> spec;
        Specification<Project> specfication = ownedBy(designerId);
        Specification<Project> rsqlSpecfication = RSQLJPASupport.toSpecification(filter);

        if (filter == null || filter.isBlank()) {
            // default view no completed tasks
            spec = specfication.and((root, cq, cb) -> cb.notEqual(root.get("status"), ProjectStatus.COMPLETED));
        } else {
            spec = specfication.and(rsqlSpecfication);
        }

        return spec;
    }

    /**
     * Filters projects by owner via client
     * 
     * @param designerId retrieves the projects assigned designer
     * @return owner's filtered project list
     */
    private Specification<Project> ownedBy(Long designerId) {
        return ((root, cq, cb) -> cb.equal(root.get("client").get("designer").get("id"), designerId));
    }

    /**
     * Returns a project using their projectId.
     * 
     * 
     * <p>
     * Fetches a specific project for operations such as updating,
     * assigning rooms or checking project status.
     * </p>
     * 
     * @param id project's unique identifier
     * @throws ProjectNotFoundException if the project is not found
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public ProjectDTO getProjectById(Long id) {

        Project project = findProject(id);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        return projectDTO;
    }

    /**
     * Finds all projects that have the same status, using the status record.
     * 
     * <p>
     * Retrieves all projects that have the same status for progress tracking.
     * 
     * </p>
     * 
     * 
     * @param status project status enum
     * @returns projects with same status
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<ProjectDTO> getProjectsByStatus(ProjectStatus status, Pageable pageable) {

        if (status == null) {
            throw new ProjectNotFoundException("projectStatus", status);
        }

        return projectRepository.findProjectsByStatus(
                status, pageable).map(project -> {
                    ProjectDTO projectDTO = projectMapper.toDto(project);
                    return projectDTO;
                });

    }

    /**
     * Returns all projects in the order of due date, using the deadline record.
     * 
     * <p>
     * Sorts all projects in order of due date to help,
     * manage progress tracking and deadlines.
     * 
     * Custom query created in the repository.
     * </p>
     * 
     * @returns order by due date
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<Deadline> sortsProjectsByDueDate(Pageable pageable) {
        return projectRepository.getAllProjectsOrderByDueDate(pageable);
    }

    /**
     * Creates a new project for a client.
     * 
     * <p>
     * Creates a new design project for specific client
     * entity for CRUD operations. It will be assigned a unique identifier.
     * </p>
     * 
     * @param project  project object to be created
     * @param clientId client's unique identifier
     * @throws IllegalArgumentException the project fields are null
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public ProjectDTO createProject(ProjectCreateDTO projectCreateDTO, Long clientId, String username) {

        Client existingClient = clientService.findClient(clientId);

        findClientByDesigner(existingClient, username);

        if (projectCreateDTO == null && clientId == null) {
            throw new IllegalArgumentException("Project must not be null");
        }

        projectCreateDTO.setClient(existingClient);
        Project project = projectMapper.toEntity(projectCreateDTO);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    /**
     * Updates a existing project.
     * 
     * <p>
     * Use this method to modify project details such as deadlines, budget or
     * description.
     * 
     * If the project status has been updated to COMPLETED the
     * completedAt field will be set with the current date.
     * </p>
     * 
     * @param id      project's unique identifier
     * @param project project object to be updated
     * @return updated project
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public ProjectDTO updateProject(Long id, ProjectUpdateDTO projectUpdateDTO, String username) {

        Project existingProject = findProject(id);

        Client existingClient = clientService.findClient(existingProject.getClient().getId());

        findClientByDesigner(existingClient, username);

        // Updated Project Status to COMPLETED, sets completedAt field
        if (existingProject.getStatus() == ProjectStatus.COMPLETED
                && existingProject.getCompletedAt() == null) {
            existingProject.setCompletedAt(Instant.now());
        }

        projectMapper.updateEntity(projectUpdateDTO, existingProject);

        return projectMapper.toDto(projectRepository.save(existingProject));
    }

    /**
     * Deletes a existing project.
     * 
     * <p>
     * This method is used when a project is canceled and should no longer be
     * tracked. Associated rooms may also be deleted.
     * </p>
     * 
     * @param id project's unique identifier
     * @return project is deleted
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public void deleteProject(Long id, String username) {
        Project project = findProject(id);
        Client existingClient = clientService.findClient(project.getClient().getId());

        findClientByDesigner(existingClient, username);

        projectRepository.delete(project);
    }

    /**
     * Reassigns Project to a existing client.
     * 
     * <p>
     * Use this method when a project was assigned to the wrong client and needs to
     * be reassigned.
     * It will update the many to one relationship.
     * </p>
     * 
     * @param clientId  client's unique identifier
     * @param projectId project's unique identifier
     * @return project is reassigned
     */
    @Transactional
    @PreAuthorize("hasRole('DESIGNER')")
    public ProjectDTO reassignClient(Long clientId, Long projectId, String username) {
        Project existingProject = findProject(projectId);
        Client client = clientService.findClient(clientId);

        findClientByDesigner(client, username);

        if (existingProject == null || client == null) {
            throw new ProjectNotFoundException("projectId", projectId);
        }

        existingProject.setClient(client);

        return projectMapper.toDto(projectRepository.save(existingProject));
    }

    /**
     * Saves project entity when room is created
     * 
     * <p>
     * Creates the One to One relationship
     * with Room, when the room is created.
     * </p>
     *
     * @param project project's object to be saved
     */

    @Transactional
    public Project saveProjectEntity(Project project) {
        return projectRepository.save(project);
    }

    /**
     * Retrieved the Project's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the project object to be deleted
     * @throws ProjectNotFoundException if the project is not found
     * @return the project
     */
    public Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("projectId", id));
    }

    public void findClientByDesigner(Client existingClient, String username) {

        User user = authenticationService.findUser(username);
        Designer designer = designerService.findDesigner(user.getId());

        if (existingClient.getDesigner().getId() != designer.getId()) {
            throw new AccessDeniedException("User does not have authorization");
        }

    }

}
