package com.interiordesignplanner.room;

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
    @NotNull(message = "Type is required")
    private RoomType type;

    // Length of the room
    @NotNull(message = "Length is required")
    private Double length;

    // Height of the room
    @NotNull(message = "Height is required")
    private Double height;

    // Width of the room
    @NotNull(message = "Width is required")
    private Double width;

    // Unit of dimensions
    @NotNull(message = "Unit is required")
    private String unit;

}
