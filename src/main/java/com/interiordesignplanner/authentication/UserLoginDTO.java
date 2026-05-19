package com.interiordesignplanner.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for user login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @Schema(description = "User username", example = "sophiethompson")
    private String username;

    @Schema(description = "User password", example = "xds6fs46yG7x9s")
    private String password;

}
