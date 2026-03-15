package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.interiordesignplanner.client.Client;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectCreateDTO {

    // Project Owner
    private Client client;

    // Project name
    @NotBlank(message = "Project name is required")
    @Size(min = 5, max = 30, message = "Project name must be between 5 and 30 characters")
    private String projectName;

    // Project status
    @NotNull(message = "Status is required")
    private ProjectStatus status;

    // Project budget
    @NotNull(message = "Budget is required")
    @Digits(integer = 6, fraction = 2, message = "Budget must be a valid amount")
    @Min(value = 0, message = "Budget must not be negative")
    private BigDecimal budget;

    // Project start date
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be before due date")
    private LocalDate startDate;

    // Project due date
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be after start date")
    private LocalDate dueDate;

    // Project description
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;

    // Project meeting url
    private String meetingURL;

}
