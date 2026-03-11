package com.interiordesignplanner.project;

import java.time.Instant;
import java.time.LocalDate;

import com.interiordesignplanner.room.RoomType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    // Project's id
    private Long id;

    // Name of Client
    private String clientName;

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

    // Video conference link for remote project meetings (Google Meets)
    private String meetingURL;

    // Project description
    private String description;

    // The timestamp of project completion
    private Instant completedAt;

    // Room name
    @Enumerated(EnumType.STRING)
    private RoomType room;

}
