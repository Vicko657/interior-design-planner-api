package com.interiordesignplanner.client;

import com.interiordesignplanner.designer.Designer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating a client")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientCreateDTO {

    @Schema(description = "Client first name", example = "Tom")
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Schema(description = "Client last name", example = "Jackson")
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    @Schema(description = "Client email address", example = "tomjackson@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email address")
    private String email;

    @Schema(description = "Client mobile number", example = "07332436482")
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{11}$", message = "Mobile number must be 11 digits")
    private String phone;

    @Schema(description = "Client address", example = "4 Darent road, London, W5 YBS")
    @NotBlank(message = "Address is required")
    private String address;

    @Schema(description = "Client notes", example = "Prefers weekend meetings")
    @Size(min = 5, max = 200, message = "Notes must be between 5 and 200 characters")
    private String notes;

    @Schema(description = "Client designer", example = "43")
    private Designer designer;

}
