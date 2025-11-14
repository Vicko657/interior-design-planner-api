package com.interiordesignplanner.project;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interiordesignplanner.AbstractEntity;
import com.interiordesignplanner.client.Client;
import com.interiordesignplanner.room.Room;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Models a design project for a client. Each project will have details
 * such as the name, status, budget, meeting link and description.
 * A project belongs to one client and can belong to one room and
 * is extending the AbstractEntity class, which provides their unique
 * identifier and timestamps for creation and updates to their data.
 */

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project extends AbstractEntity {

    // Foreign key to Client entity, many to one bidirectional relationship.
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonBackReference
    private Client client;

    // Name of the project
    private String projectName;

    // Status of the project
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    // Estimated cost of project
    private Integer budget;

    // The start date and due date to help with planning and progress tracking
    private LocalDate startDate;
    private LocalDate dueDate;

    // Brief project description to help the designer have an overview
    private String description;

    // Video conference link for remote project meetings (Google Meets)
    private String meetingURL;

    // The timestamp of project completion
    private Instant completedAt;

    // Creates One to One Bidirectional relationship with the room entity
    @OneToOne(mappedBy = "project")
    @JsonManagedReference
    private Room room;

    // Constructor
    public Project(String projectName, ProjectStatus status, Integer budget, String description, String meetingURL,
            LocalDate startDate,
            LocalDate dueDate) {

        this.projectName = projectName;
        this.status = status;
        this.budget = budget;
        this.description = description;
        this.meetingURL = meetingURL;
        this.startDate = startDate;
        this.dueDate = dueDate;

    }

    // Getters
    @Override
    public Long getId() {
        return super.getId();
    }

}
