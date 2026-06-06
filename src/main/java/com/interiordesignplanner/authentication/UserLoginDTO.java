package com.interiordesignplanner.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @Schema(description = "User username", example = "sophiethompson")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "User password", example = "xds6fs46yG7x9s")
    @NotBlank(message = "Password is required")
    private String password;

}
