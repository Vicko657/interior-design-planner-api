package com.interiordesignplanner.designer;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName(value = "Designer Controller Test Suite")
public class DesignerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesignerRepository designerRepository;

    @Autowired
    private UserRepository userRepository;

    // Converts the designerProfileDTO into JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Designer designer;

    private DesignerProfileUpdateDTO designerProfileUpdateDTO;

    @BeforeEach
    void setUp() {

        designerRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmailAddress("samwilliams@gmail.com");
        user.setPhoneNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("sam");
        user.setPassword(passwordEncoder.encode("huwa71egyw"));
        userRepository.save(user);

        designer = new Designer();
        designer.setUser(user);
        designer.setBio("Interior designer with a love of pushing the goalpost of designs");
        designer.setProfileImage("/user/sam_img.png");
        designer.setExperience(5);
        designer.setLocation("London");
        designerRepository.save(designer);

    }

    @Test
    @DisplayName("GetProfile: Should return authenticated designer's profile")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetProfile() throws Exception {

        mockMvc.perform(get("/api/designer/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sam Williams")))
                .andExpect(jsonPath("$.profileImage", is("/user/sam_img.png")));

    }

    @Test
    @DisplayName("UpdateProfile: Designer's emailAddress is updated")
    @WithUserDetails(value = "sam", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUpdateProfile() throws Exception {

        designerProfileUpdateDTO = new DesignerProfileUpdateDTO();
        designerProfileUpdateDTO.setEmailAddress("saminteriors@gmail.com");

        mockMvc.perform(put("/api/designer/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        designerProfileUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", is("saminteriors@gmail.com")));

    }

}
