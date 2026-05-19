package com.interiordesignplanner.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a client summary dashboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryDTO {

    @Schema(description = "Client id", example = "8")
    private Long id;

    @Schema(description = "Client full name", example = "Tom Jackson")
    private String fullName;

    @Schema(description = "Client email address", example = "tomjackson@gmail.com")
    private String emailAddress;

    @Schema(description = "Client phone number", example = "07332436482")
    private String phoneNumber;

    @Schema(description = "Client address", example = "4 Darent road, London, W5 YBS")
    private String address;

    @Schema(description = "Client total projects", example = "2")
    private Long totalProjects;

    @Schema(description = "Client notes", example = "Prefers weekend meetings")
    private String notes;

}
