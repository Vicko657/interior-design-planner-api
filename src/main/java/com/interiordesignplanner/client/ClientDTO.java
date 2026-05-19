package com.interiordesignplanner.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    @Schema(description = "Client id", example = "8")
    private Long id;

    @Schema(description = "Client first name", example = "Tom")
    private String firstName;

    @Schema(description = "Client last name", example = "Jackson")
    private String lastName;

    @Schema(description = "Client email address", example = "tomjackson@gmail.com")
    private String email;

    @Schema(description = "Client mobile number", example = "07332436482")
    private String phone;

    @Schema(description = "Client address", example = "4 Darent road, London, W5 YBS")
    private String address;

    @Schema(description = "Client notes", example = "Prefers weekend meetings")
    private String notes;

    @Schema(description = "Client total projects", example = "2")
    private Integer totalProjects;

    @Schema(description = "Client designer name", example = "Sophie Thompson")
    private String designer;

}
