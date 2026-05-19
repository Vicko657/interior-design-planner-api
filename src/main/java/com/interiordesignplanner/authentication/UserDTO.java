package com.interiordesignplanner.authentication;

import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Response body for a user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    @Schema(description = "User id", example = "43")
    private Long id;

    @Schema(description = "User first name", example = "Sophie")
    private String firstName;

    @Schema(description = "User last name", example = "Thompson")
    private String lastName;

    @Schema(description = "User email address", example = "sophie.thompson@gmail.com")
    private String emailAdress;

    @Schema(description = "User mobile number", example = "07554362738")
    private String phoneNumber;

    @Schema(description = "User username", example = "sophiethompson")
    private String username;

    @Schema(description = "User password", example = "xds6fs46yG7x9s")
    private String password;

    @Schema(description = "User role", example = "DESIGNER")
    @Enumerated(EnumType.STRING)
    private Roles roles;
}
