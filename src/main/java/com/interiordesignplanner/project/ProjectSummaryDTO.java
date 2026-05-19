package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a project summary dashboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDTO {

    @Schema(description = "Project id", example = "1")
    private Long id;

    @Schema(description = "Client full name", example = "Tom Jackson")
    private String clientName;

    @Schema(description = "Project name", example = "Coastal Living Room")
    private String projectName;

    @Schema(description = "Project status", example = "PLANNING")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Schema(description = "Project budget", example = "5000.00")
    private BigDecimal budget;

    @Schema(description = "Project start date", example = "2026-01-08")
    private LocalDate startDate;

    @Schema(description = "Project due date", example = "2026-09-24")
    private LocalDate dueDate;

    @Schema(description = "Project description", example = "Full redesign of living room with contemporary furniture and lighting")
    private String description;

}
