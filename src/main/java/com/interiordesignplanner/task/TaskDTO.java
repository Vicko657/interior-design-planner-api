package com.interiordesignplanner.task;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @Schema(description = "Task name", example = "Order lighting and furniture")
    private String taskName;

    @Schema(description = "Task description", example = "Check the inventory for the items")
    private String task;

    @Schema(description = "Task description", example = "2026-03-02")
    private LocalDate date;

    @Schema(description = "Task completed?", example = "true")
    private boolean completed;

    @Schema(description = "Project name", example = "Luxury Master Bedroom")
    private String projectName;

}
