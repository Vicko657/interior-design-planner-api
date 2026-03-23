package com.interiordesignplanner.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interiordesignplanner.designer.DesignerRepository;
import com.interiordesignplanner.mapper.UserMapper;
import com.interiordesignplanner.security.ApplicationUserDetails;
import com.interiordesignplanner.security.ApplicationUserDetailsService;
import com.interiordesignplanner.security.JwtService;

/**
 * Unit tests for {@link AuthenticationService}.
 *
 * <p>
 * Verifies user registration and logging in.
 * Ensures that validation rules and exception handling (such as
 * {@code BadCredientialsException}) work as expected.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Authentication Service Test Suite")
public class AuthenticationServiceTest {

    // Mock client repository
    @Mock
    public UserRepository userRepository;

    // User mapper
    @Mock
    private UserMapper userMapper;

    // Mock designer repository
    @Mock
    private DesignerRepository designerRepository;

    // Mock authentication manager
    @Mock
    private AuthenticationManager authenticationManager;

    // Mock authentication manager
    @Mock
    private PasswordEncoder passwordEncoder;

    // Mock jwtservice
    @Mock
    private JwtService jwtService;

    // Mock application user details service
    @Mock
    private ApplicationUserDetailsService applicationUserDetailsService;

    // Mock application user details service
    @Mock
    private CompromisedPasswordChecker compromisedPasswordChecker;

    @Mock
    private CompromisedPasswordDecision decision;

    // Mock authentication service
    @InjectMocks
    private AuthenticationService authenticationService;

    private UserCreateDTO userCreateDTO;

    private UserLoginDTO userLoginDTO;

    private User user1;

    @BeforeEach
    public void setUp() {

        // Added User Mapper to convert dtos and entities
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.userMapper = new UserMapper(modelMapper);

        authenticationService = new AuthenticationService(authenticationManager, jwtService, userRepository, userMapper,
                designerRepository, applicationUserDetailsService, passwordEncoder, compromisedPasswordChecker);

        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("Sam");
        userCreateDTO.setLastName("Williams");
        userCreateDTO.setEmail("samwilliams@gmail.com");
        userCreateDTO.setMobileNumber("07348294736");
        userCreateDTO.setRoles(Roles.DESIGNER);
        userCreateDTO.setUsername("samx823");
        userCreateDTO.setPassword("huwa71egyw");

        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Sam");
        user1.setLastName("Williams");
        user1.setEmail("samwilliams@gmail.com");
        user1.setMobileNumber("07348294736");
        user1.setRoles(Roles.DESIGNER);
        user1.setUsername("samx823");
        user1.setPassword("huwa71egyw");

    }

    /**
     * Tests for registering a new User successfully
     */
    @Test
    @DisplayName("RegisterUser: Registers new User")
    public void testRegisterUser_ReturnsCreated() {

        // Arrange: Mock UserCreateDTO, CompromisedPasswordChecker, User and password
        // hashing

        // Safe password
        decision = mock(CompromisedPasswordDecision.class);
        when(decision.isCompromised()).thenReturn(false);
        when(compromisedPasswordChecker.check(any())).thenReturn(decision);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Act: Registering designer with service method
        authenticationService.registerDesigner(userCreateDTO);

        // Assert: Verifies that user has been created
        verify(userRepository, times(1)).save(any(User.class));

    }

    /**
     * Tests if password is compromised
     */
    @Test
    @DisplayName("RegisterUser: User password is compromised")
    public void testRegisterUser_WithCompromisedPassword() {

        // Arrange: Mock error message and UserCreateDTO (password is compromised)
        String errorMessage = "This password cannot be used and is compromised.";

        decision = mock(CompromisedPasswordDecision.class);

        when(decision.isCompromised()).thenReturn(true);
        when(compromisedPasswordChecker.check(any())).thenReturn(decision);

        /// Act: Queries if the exception is thrown if the password the user entered is
        /// compromised
        CompromisedPasswordException exception = assertThrows(CompromisedPasswordException.class, () -> {
            authenticationService.registerDesigner(userCreateDTO);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);

    }

    /**
     * Tests if User login is valid
     */
    @Test
    @DisplayName("LoginUser: Login existing User")
    public void testLoginUser_ReturnsValid() {

        // Arrange: Mock User login, authentication and generating token
        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("samx823");
        userLoginDTO.setPassword("huwa71egyw");
        String username = userLoginDTO.getUsername();

        when(applicationUserDetailsService.loadUserByUsername(username)).thenReturn(new ApplicationUserDetails(user1));

        when(jwtService.generateJwtToken(any())).thenReturn("newToken");

        // Act: generating new token with user details
        String token = authenticationService.login(userLoginDTO);

        // Assert: Verifies that user credentials are valid and token is generated
        assertNotNull(token);
        assertThat(token).isEqualTo("newToken");
        verify(authenticationManager, times(1)).authenticate(any());

    }

    /**
     * Tests if User login is invalid
     */
    @Test
    @DisplayName("LoginUser: User is invalid")
    public void testLoginUser_ReturnsInvalid() {

        // Arrange: Mock error message and User login (wrong password)
        String errorMessage = "Invalid username and password";

        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("samx823");
        userLoginDTO.setPassword("shdhfsdkj");

        // Act: Queries if the exception is thrown if user is not authenticated
        doThrow(new BadCredentialsException("Invalid username and password")).when(
                authenticationManager).authenticate(any());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(userLoginDTO);
        });

        // Assert: Verifies exception matches the thrown exception
        assertThat(exception.getMessage()).isEqualTo(errorMessage);

    }

}
