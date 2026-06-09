package com.interiordesignplanner.designer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignerProfileUpdateDTO {

    @Schema(description = "Designer's first name", example = "Sophie")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Schema(description = "Designer's last name", example = "Thompson")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    @Schema(description = "Designer's email address", example = "sophie.thompson@gmail.com")
    @Email(message = "Invaild email address")
    private String emailAddress;

    @Schema(description = "Designer's phone number", example = "07554362738")
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
    private String phoneNumber;

    @Schema(description = "Designer's profile image", example = "/img/sophie.png")
    private String profileImage;

    @Schema(description = "Designer's bio", example = "I design for the ones who collect, layer and live loudly")
    @Size(min = 5, max = 200, message = "Bio must be between 5 and 200 characters")
    private String bio;

    @Schema(description = "Designer's experience", example = "6")
    private Integer experience;

    @Schema(description = "Designer's location", example = "London")
    @Size(min = 3, max = 15, message = "Location must be between 3 and 15 characters")
    private String location;

}
