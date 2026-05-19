package com.interiordesignplanner.room;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    @Schema(description = "Room id", example = "1")
    private Long id;

    @Schema(description = "Project name", example = "Coastal Living Room")
    private String projectName;

    @Schema(description = "Room type", example = "LIVING_ROOM")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Schema(description = "Room length", example = "6.4")
    private Double length;

    @Schema(description = "Room height", example = "5.0")
    private Double height;

    @Schema(description = "Room width", example = "3.5")
    private Double width;

    @Schema(description = "Unit of dimensions", example = "M")
    private String unit;

    @Schema(description = "Room Checklist", example = "Order lighting and furniture, Check the inventory for the items, 2026-03-02, true")
    private List<Task> checklist = new ArrayList<>();

    @Schema(description = "Room Inventory", example = "https://cdn.sklum.com/uk/wk/5304236/wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.jpg?cf-resize=gallery, Isasia table lamp, Wireless LED outdoor table lamp in iron and natural stone,  40.00,  1,  height: 15cm, diameter: 12 cm, https://www.sklum.com/uk/buy-outdoor-table-lamps/220716-wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.html, false}")
    private List<Item> inventory = new ArrayList<>();

}
