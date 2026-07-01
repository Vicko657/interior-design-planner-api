package com.interiordesignplanner.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.interiordesignplanner.room.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a project summary dashboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDTO {

    @Schema(description = "Project id", example = "1")
    private Long id;

    @Schema(description = "Client id", example = "2")
    private Long clientId;

    @Schema(description = "Client full name", example = "Tom Jackson")
    private String clientName;

    @Schema(description = "Project name", example = "Coastal Living Room")
    private String projectName;

    @Schema(description = "Project status", example = "PLANNING")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Schema(description = "Project budget", example = "5000.00")
    private BigDecimal budget;

    @Schema(description = "Project remianing budget", example = "3500.00")
    private BigDecimal remainingBudget;

    @Schema(description = "Project start date", example = "2026-01-08")
    private LocalDate startDate;

    @Schema(description = "Project due date", example = "2026-09-24")
    private LocalDate dueDate;

    @Schema(description = "Project description", example = "Full redesign of living room with contemporary furniture and lighting")
    private String description;

    @Schema(description = "Room id", example = "50")
    private Long roomId;

    @Schema(description = "Room type", example = "LIVING_ROOM")
    @Enumerated(EnumType.STRING)
    private RoomType room;

    @Schema(description = "Room length", example = "6.4")
    private Double length;

    @Schema(description = "Room height", example = "5.0")
    private Double height;

    @Schema(description = "Room width", example = "3.5")
    private Double width;

    @Schema(description = "Unit of dimensions", example = "M")
    private String unit;

}
