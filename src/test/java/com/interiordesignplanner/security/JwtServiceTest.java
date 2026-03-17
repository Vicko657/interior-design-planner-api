package com.interiordesignplanner.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;

/**
 * Unit tests for {@link JwtService}.
 *
 * <p>
 * Verifies user generation of JWT Tokens
 * (such as
 * {@code ClientNotFoundException}) work as expected.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Jwt Service Test Suite")
public class JwtServiceTest {

    // Mock jwt service
    @InjectMocks
    private JwtService jwtService;

    // Mock user details
    private ApplicationUserDetails applicationUserDetails, applicationUserDetails2;

    private User user, user2;

    @BeforeEach
    public void setUp() {

        // Mocked env variables - secret and expiration
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "1829964f4474365ddff9271dfa7652c37aef848c96f11c12701f2d4077f3dc50");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        // Mocked new user
        user = new User();
        user.setUsername("jessica79");
        user.setRoles(Roles.DESIGNER);

        // Mocked user details
        applicationUserDetails = new ApplicationUserDetails(user);

    }

    /**
     * Tests generating a token is successful
     */
    @Test
    @DisplayName("GeneratedToken: Returns new Token")
    public void testTokenGeneration_ReturnsToken() {

        // Arrange: applicationUserDetails

        // Act: Generating new jwtToken
        String token = jwtService.generateJwtToken(applicationUserDetails);

        // Assert: Verifies that the token is not null
        assertNotNull(token);

    }

    /**
     * Tests extracting a username is successful
     */
    @Test
    @DisplayName("ExtractingUser: Returns Username")
    public void testUsernameExtraction_ReturnsUsername() {

        // Arrange: Generating new token
        String token = jwtService.generateJwtToken(applicationUserDetails);

        // Act: Extracting username
        String username = jwtService.extractUsername(token);

        // Assert: Verifies that the username is not null and is equal the user's
        // username
        assertNotNull(username);
        assertThat(user.getUsername()).isEqualTo(username);

    }

    /**
     * Tests if validation of token is true
     */
    @Test
    @DisplayName("IsTokenValid: Returns True")
    public void testTokenValidation_ReturnsTrue() {

        // Arrange: Generating new token
        String token = jwtService.generateJwtToken(applicationUserDetails);

        // Act: Validating token
        Boolean isTokenValid = jwtService.isTokenValid(token, applicationUserDetails);

        // Assert: Verifies that token is valid - true
        assertTrue(isTokenValid);

    }

    /**
     * Tests if validation of token is false
     */
    @Test
    @DisplayName("IsTokenValid: Returns False")
    public void testTokenValidation_ReturnsFalse() {

        // Arrange: Generating new token, mocked new user and new user details
        String token = jwtService.generateJwtToken(applicationUserDetails);

        // Mocked new user
        user2 = new User();
        user2.setUsername("sam620");
        user2.setRoles(Roles.ADMIN);

        applicationUserDetails2 = new ApplicationUserDetails(user2);

        // Act: Validating token with different user details
        Boolean isTokenValid = jwtService.isTokenValid(token, applicationUserDetails2);

        // Assert: Verifies that token is not valid - false
        assertFalse(isTokenValid);

    }

}
