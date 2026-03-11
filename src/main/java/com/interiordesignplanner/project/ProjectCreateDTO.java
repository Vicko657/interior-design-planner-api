package com.interiordesignplanner.project;

import java.time.LocalDate;

import com.interiordesignplanner.client.Client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String projectName;

    // Project status
    @NotNull(message = "Project status is required")
    private ProjectStatus status;

    // Project budget
    @NotNull(message = "Project's budget is required")
    private Integer budget;

    // Project start date
    @NotNull(message = "Project start date is required")
    private LocalDate startDate;

    // Project due date
    @NotNull(message = "Project deadline is required")
    private LocalDate dueDate;

    // Project description
    @NotBlank(message = "Project description is required")
    private String description;

    // Project meeting url
    private String meetingURL;

}
