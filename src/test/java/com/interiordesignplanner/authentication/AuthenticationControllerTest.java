package com.interiordesignplanner.authentication;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interiordesignplanner.designer.DesignerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName(value = "Authentication Controller Test Suite")
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // Converts the UserDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerRepository designerRepository;

    @BeforeEach
    void setUp() {

        designerRepository.deleteAll();
        userRepository.deleteAll();

    }

    /**
     * Register User: Tests expected response code when a user has been
     * registered
     */
    @Test
    @DisplayName("RegisterUser: User is registered")
    void testRegisterUser_Created_201() throws Exception {

        // Given
        UserCreateDTO user = new UserCreateDTO();
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmail("samwilliams@gmail.com");
        user.setMobileNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("samx823");
        user.setPassword("huwa71egyw");

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration successful"));

    }

    /**
     * Duplicate User: Tests expected response code when a 2nd user with similar
     * details attempts to register
     */
    @Test
    @DisplayName("DuplicateUser: User with existing mobile number")
    void testDuplicateUser_BadRequest_400() throws Exception {

        // Given
        testRegisterUser_Created_201();

        UserCreateDTO user2 = new UserCreateDTO();
        user2.setFirstName("Lucy");
        user2.setLastName("Williams");
        user2.setEmail("lucywill@gmail.com");
        user2.setMobileNumber("07348294736");
        user2.setRoles(Roles.DESIGNER);
        user2.setUsername("lucy253");
        user2.setPassword("wgdsuygdqw93");

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        user2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed"));

    }

    /**
     * Valid Login User: Tests expected response code when a user logs in
     */
    @Test
    @DisplayName("LoginUser: Valid Login")
    void testLoginUser_Ok_200() throws Exception {

        // Given
        testRegisterUser_Created_201();

        UserLoginDTO login = new UserLoginDTO();
        login.setUsername("samx823");
        login.setPassword("huwa71egyw");

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", is("Successfully logged in")));

    }

    /**
     * Invalid Login User: Tests expected response code when a user logs in with
     * wrong credientials
     */
    @Test
    @DisplayName("LoginUser: Invalid Login")
    void testLoginUser_Unauthorized_401() throws Exception {

        // Given
        testRegisterUser_Created_201();

        UserLoginDTO login = new UserLoginDTO();
        login.setUsername("lucy253");
        login.setPassword("wgdsuygdqw93");

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

    }

}
