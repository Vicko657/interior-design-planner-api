package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDTO {

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
    private BigDecimal budget;

    // The start date and due date to help with planning and progress tracking
    private LocalDate startDate;
    private LocalDate dueDate;

    // Project description
    private String description;

}
