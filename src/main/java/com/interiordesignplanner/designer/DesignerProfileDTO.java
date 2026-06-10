package com.interiordesignplanner.designer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignerProfileDTO {

    @Schema(description = "Designer's id", example = "8")
    private Long id;

    @Schema(description = "Designer's fullname", example = "Sophie Thompson")
    private String name;

    @Schema(description = "Designer's first name", example = "Sophie")
    private String firstName;

    @Schema(description = "Designer's last name", example = "Thompson")
    private String lastName;

    @Schema(description = "Designer's email address", example = "sophie.thompson@gmail.com")
    private String emailAddress;

    @Schema(description = "Designer's phone number", example = "07554362738")
    private String phoneNumber;

    @Schema(description = "Designer's profile image", example = "/img/sophie.png")
    private String profileImage;

    @Schema(description = "Designer's bio", example = "I design for the ones who collect, layer and live loudly")
    private String bio;

    @Schema(description = "Designer's experience", example = "6")
    private Integer experience;

    @Schema(description = "Designer's location", example = "London")
    private String location;

}
