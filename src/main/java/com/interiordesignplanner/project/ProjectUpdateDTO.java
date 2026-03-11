package com.interiordesignplanner.project;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProjectUpdateDTO {

    // Project name
    private String projectName;

    // Project status
    private ProjectStatus status;

    // Project budget
    private Integer budget;

    // Project start date
    private LocalDate startDate;

    // Project due date
    private LocalDate dueDate;

    // Project description
    private String description;

    // Project meeting url
    private String meetingURL;

}
