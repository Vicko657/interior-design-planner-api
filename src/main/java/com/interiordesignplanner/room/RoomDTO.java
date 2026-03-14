package com.interiordesignplanner.room;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    // Room's id
    private Long id;

    // Name of the project
    private String projectName;

    // Categories the specific type of room
    @Enumerated(EnumType.STRING)
    private RoomType type;

    // Dimemsions of the room
    private Double length;
    private Double height;
    private Double width;

    private String unit;

    // Checklist of tasks specific to the room
    private List<Task> checklist = new ArrayList<>();

    // Records design updates to the room over time
    private List<String> changes = new ArrayList<>();

}
