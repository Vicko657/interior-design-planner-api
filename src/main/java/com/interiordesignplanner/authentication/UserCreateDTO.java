package com.interiordesignplanner.authentication;

import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Request body for creating a user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCreateDTO {

    @Schema(description = "User first name", example = "Sophie")
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Schema(description = "User last name", example = "Thompson")
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    @Schema(description = "User email address", example = "sophie.thompson@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email address")
    private String email;

    @Schema(description = "User mobile number", example = "07554362738")
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{11}$", message = "Mobile number must be 11 digits")
    private String mobileNumber;

    @Schema(description = "User username", example = "sophiethompson")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String username;

    @Schema(description = "User password", example = "xds6fs46yG7x9s")
    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be more 5 characters")
    private String password;

    @Schema(description = "User role", example = "DESIGNER")
    @Enumerated(EnumType.STRING)
    private Roles roles;

}
