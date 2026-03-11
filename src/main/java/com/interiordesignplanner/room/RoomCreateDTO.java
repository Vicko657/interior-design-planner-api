package com.interiordesignplanner.room;

import java.util.List;

import com.interiordesignplanner.project.Project;

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
public class RoomCreateDTO {

    // Project the room is assigned
    private Project project;

    // Project's room
    @NotNull(message = "Room type is required")
    private RoomType type;

    // Length of the room
    @NotNull(message = "Room length is required")
    private Double length;

    // Height of the room
    @NotNull(message = "Room height is required")
    private Double height;

    // Width of the room
    @NotNull(message = "Room width is required")
    private Double width;

    // Unit of dimensions
    @NotNull(message = "Room unit is required")
    private String unit;

    // Room's Checklist
    private List<String> checklist;

    // Room's design updates over time
    private List<String> changes;

}
