package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectUpdateDTO {

    // Project name
    @Size(min = 5, max = 30, message = "Project name must be between 5 and 30 characters")
    private String projectName;

    // Project status
    private ProjectStatus status;

    // Project budget
    @Digits(integer = 6, fraction = 2, message = "Budget must be a valid amount")
    @Min(value = 0, message = "Budget must not be negative")
    private BigDecimal budget;

    // Project start date
    @FutureOrPresent(message = "Start date must be before due date")
    private LocalDate startDate;

    // Project due date
    @Future(message = "Due date must be after start date")
    private LocalDate dueDate;

    // Project description
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;

    // Project meeting url
    private String meetingURL;

}
