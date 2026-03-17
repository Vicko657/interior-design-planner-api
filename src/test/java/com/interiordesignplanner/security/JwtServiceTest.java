package com.interiordesignplanner.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private ApplicationUserDetails applicationUserDetails;

    private User user;

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
    @DisplayName("GeneratedToken: Returns all of the clients in the database")
    public void testTokenGeneration_ReturnsToken() {

        // Arrange: applicationUserDetails

        // Act: Generating new jwtToken
        String token = jwtService.generateJwtToken(applicationUserDetails);

        // Assert: Verifies that the token is not null
        assertNotNull(token);

    }

}
