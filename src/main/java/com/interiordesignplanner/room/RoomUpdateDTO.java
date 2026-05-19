package com.interiordesignplanner.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Request body for updating a room")
@Data
public class RoomUpdateDTO {

    @Schema(description = "Room type", example = "LIVING_ROOM")
    private RoomType type;

    @Schema(description = "Room length", example = "6.4")
    private Double length;

    @Schema(description = "Room height", example = "5.0")
    private Double height;

    @Schema(description = "Room width", example = "3.5")
    private Double width;

    @Schema(description = "Unit of dimensions", example = "M")
    private String unit;

}
