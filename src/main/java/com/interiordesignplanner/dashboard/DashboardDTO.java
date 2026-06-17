package com.interiordesignplanner.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.interiordesignplanner.project.ProjectStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    @Schema(description = "Designer's fullname", example = "Sophie Thompson")
    private String name;

    @Schema(description = "Total number of active projects", example = "3")
    private Integer activeProjects;

    @Schema(description = "List of current projects")
    private List<ProjectProgress> projectProgress;

    @Schema(description = "Total number of projects to date", example = "5")
    private Integer totalProjects;

    @Schema(description = "Total number of completed projects", example = "2")
    private Integer completedProjects;

    @Schema(description = "List of recent tasks")
    private List<RecentTasks> recentTasks;

    @Schema(description = "List of client total budget")
    private List<Budget> budget;

    public static record ProjectProgress(String projectName, ProjectStatus status, LocalDate date) {

    }

    public static record RecentTasks(String taskName, Boolean isCompleted, String project) {

    }

    public static record Budget(BigDecimal budget, String clientName) {

    }

}
