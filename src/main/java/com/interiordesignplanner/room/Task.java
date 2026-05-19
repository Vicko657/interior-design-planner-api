package com.interiordesignplanner.room;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating and updating a task")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Task {

    @Schema(description = "Task name", example = "Order lighting and furniture")
    @NotNull(message = "Task name is required")
    @Size(min = 5, max = 30, message = "Task name must be between 5 and 30 characters")
    private String taskName;

    @Schema(description = "Task description", example = "Check the inventory for the items")
    @NotNull(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String task;

    @Schema(description = "Task description", example = "2026-03-02")
    @NotNull(message = "Date is required")
    private LocalDate date;

    @Schema(description = "Task completed?", example = "true")
    private boolean completed;

}
