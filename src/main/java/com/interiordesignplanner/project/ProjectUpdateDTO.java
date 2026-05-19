package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for updating a project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateDTO {

    @Schema(description = "Project name", example = "Coastal Living Room")
    @Size(min = 5, max = 30, message = "Project name must be between 5 and 30 characters")
    private String projectName;

    @Schema(description = "Project status", example = "PLANNING")
    private ProjectStatus status;

    @Schema(description = "Project budget", example = "5000.00")
    @Digits(integer = 6, fraction = 2, message = "Budget must be a valid amount")
    @Min(value = 0, message = "Budget must not be negative")
    private BigDecimal budget;

    @Schema(description = "Project start date", example = "2026-01-08")
    @PastOrPresent(message = "Start date must be before due date")
    private LocalDate startDate;

    @Schema(description = "Project due date", example = "2026-09-24")
    @Future(message = "Due date must be after start date")
    private LocalDate dueDate;

    @Schema(description = "Project description", example = "Full redesign of living room with contemporary furniture and lighting")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;

    @Schema(description = "Meeting link", example = "https://zoom.us/k/12345627")
    private String meetingURL;

}
