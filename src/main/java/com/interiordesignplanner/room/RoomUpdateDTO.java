package com.interiordesignplanner.room;

import java.util.List;

import lombok.Data;

@Data
public class RoomUpdateDTO {

    // Project's room
    private RoomType type;

    // Length of the room
    private Double length;

    // Height of the room
    private Double height;

    // Width of the room
    private Double width;

    // Unit of dimensions
    private String unit;

    // Room's Checklist
    private List<String> checklist;

    // Room's design updates over time
    private List<String> changes;

}
