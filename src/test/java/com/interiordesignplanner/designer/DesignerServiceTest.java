package com.interiordesignplanner.designer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.authentication.Roles;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.mapper.DesignerMapper;

/**
 * Unit tests for {@link DesignerService}.
 *
 * <p>
 * Verifies designers profile, updating, and deletion logic.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Designer Service Test Suite")
public class DesignerServiceTest {

    // Mock designer repository
    @Mock
    public DesignerRepository designerRepository;

    // Designer mapper
    private DesignerMapper designerMapper;

    // Mock designer service
    @InjectMocks
    private DesignerService designerService;

    // Mock user repository
    @Mock
    public UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    private Designer designer;

    @BeforeEach
    public void setUp() {

        // Added Designer Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Designer.class, DesignerProfileDTO.class).setPostConverter(convert -> {
            Designer source = convert.getSource();
            DesignerProfileDTO destination = convert.getDestination();

            if (source.getUser() != null) {
                destination.setName((source.getUser().getFirstName() + " " + source.getUser().getLastName()));
            }
            if (source.getUser() != null) {
                destination.setEmailAddress((source.getUser().getEmailAddress()));
            }
            if (source.getUser() != null) {
                destination.setPhoneNumber((source.getUser().getPhoneNumber()));
            }

            return destination;
        });

        modelMapper.createTypeMap(DesignerProfileUpdateDTO.class, Designer.class).setPostConverter(convert -> {
            DesignerProfileUpdateDTO source = convert.getSource();
            Designer destination = convert.getDestination();

            if (source.getFirstName() != null) {
                destination.getUser().setFirstName((source.getFirstName()));
            }
            if (source.getLastName() != null) {
                destination.getUser().setLastName((source.getLastName()));
            }
            if (source.getEmailAddress() != null) {
                destination.getUser().setEmailAddress((source.getEmailAddress()));
            }
            if (source.getPhoneNumber() != null) {
                destination.getUser().setPhoneNumber((source.getPhoneNumber()));
            }

            return destination;
        });

        designerMapper = new DesignerMapper(modelMapper);

        designerService = new DesignerService(designerRepository, designerMapper,
                userRepository);

        user = new User();
        user.setId(1L);
        user.setFirstName("Sam");
        user.setLastName("Williams");
        user.setEmailAddress("samwilliams@gmail.com");
        user.setPhoneNumber("07348294736");
        user.setRoles(Roles.DESIGNER);
        user.setUsername("sam");
        user.setPassword(passwordEncoder.encode("huwa71egyw"));

        designer = new Designer();
        designer.setId(1L);
        designer.setUser(user);

    }

    /**
     * Tests if Get profile returns designer's profile
     */
    @Test
    @DisplayName("GetProfile: Returns authenticated designer's profile")
    public void testGetProfile_ReturnsDesignerProfile() {

        // Arrange: mocks the repository and returns designer's profile

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        when(designerRepository.findByUserId(user.getId())).thenReturn(Optional.of(designer));

        designerMapper.toDto(designer);

        // Act: Query the service layer to check the designer is authenticate and return
        // their profile
        DesignerProfileDTO result = designerService.getProfile(user.getUsername());

        // Assert: Verifies that the result is not null and profile is retrieved
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sam Williams");
        assertThat(result.getEmailAddress()).isEqualTo("samwilliams@gmail.com");
        assertThat(result.getPhoneNumber()).isEqualTo("07348294736");

    }

    /**
     * Tests for updating auth designer profile
     */
    @Test
    @DisplayName("UpdateProfile: Updates auth designer profile")
    public void testUpdateProfile_ReturnsUpdated() {
        // Arrange: mocks the repository and profile fields to update

        DesignerProfileUpdateDTO updatedProfile = new DesignerProfileUpdateDTO();

        updatedProfile.setBio("Interior designer with a love of pushing the goalpost of designs");
        updatedProfile.setProfileImage("/user/sam_img.png");
        updatedProfile.setExperience(5);
        updatedProfile.setLocation("London");
        updatedProfile.setFirstName("Samuel");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        when(designerRepository.findByUserId(user.getId())).thenReturn(Optional.of(designer));

        when(userRepository.save(user)).thenReturn(user);

        when(designerRepository.save(designer)).thenReturn(designer);

        // Act: Query the service layer to check the designer is authenticated and
        // update the profile
        // details
        DesignerProfileDTO result = designerService.updateProfile(updatedProfile, user.getUsername());

        // Assert: Verifies that the profile was updated
        assertNotNull(result);
        assertEquals(result.getName(), "Samuel Williams");
        assertEquals(result.getBio(), "Interior designer with a love of pushing the goalpost of designs");
        assertEquals(user.getFirstName(), "Samuel");
        verify(designerRepository).save(designer);

    }

    // Reset all mock objects
    @AfterEach
    public void tearDown() {
        reset(designerRepository);
    }

}
