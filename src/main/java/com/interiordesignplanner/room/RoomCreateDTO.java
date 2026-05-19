package com.interiordesignplanner.room;

import com.interiordesignplanner.project.Project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating a room")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomCreateDTO {

    @Schema(description = "Project", example = "1")
    private Project project;

    @Schema(description = "Room type", example = "LIVING_ROOM")
    @NotNull(message = "Type is required")
    private RoomType type;

    @Schema(description = "Room length", example = "6.4")
    @NotNull(message = "Length is required")
    private Double length;

    @Schema(description = "Room height", example = "5.0")
    @NotNull(message = "Height is required")
    private Double height;

    @Schema(description = "Room width", example = "3.5")
    @NotNull(message = "Width is required")
    private Double width;

    @Schema(description = "Unit of dimensions", example = "M")
    @NotNull(message = "Unit is required")
    private String unit;

}
