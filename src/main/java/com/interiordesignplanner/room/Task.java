package com.interiordesignplanner.room;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
// Constructs a Room task
public class Task {

    // Name of Task
    @NotNull(message = "Task name is required")
    @Size(min = 5, max = 30, message = "Task name must be between 5 and 30 characters")
    private String taskName;

    // Task Description
    @NotNull(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String task;

    // Date the task needs to be completed
    @NotNull(message = "Date is required")
    private LocalDate date;

    // Task completed?
    private boolean completed;

}
