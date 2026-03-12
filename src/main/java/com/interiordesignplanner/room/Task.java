package com.interiordesignplanner.room;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
// Constructs a Room task
public class Task {

    // Name of Task
    private String taskName;

    // Task Description
    private String task;

    // Date the task needs to be completed
    private LocalDate date;

    // Task completed?
    private boolean completed;

}
