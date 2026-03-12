package com.interiordesignplanner.room;

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

}
