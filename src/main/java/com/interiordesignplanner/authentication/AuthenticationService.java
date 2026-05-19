package com.interiordesignplanner.authentication;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;
import com.interiordesignplanner.exceptions.UserExistsException;
import com.interiordesignplanner.exceptions.UserNotFoundException;
import com.interiordesignplanner.mapper.UserMapper;
import com.interiordesignplanner.security.ApplicationUserDetails;
import com.interiordesignplanner.security.ApplicationUserDetailsService;
import com.interiordesignplanner.security.JwtService;

import jakarta.transaction.Transactional;

/**
 * Authentication service class provides business logic
 * and operations relating to a user.
 * 
 * <p>
 * Registers, Logs in user, validates user information and
 * manages the relationships between clients and users.
 * 
 * Serves as an interface between controllers and the persistence layer.
 * </p>
 */
@Service
public class AuthenticationService {

    private final DesignerRepository designerRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ApplicationUserDetailsService applicationUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository, UserMapper userMapper, DesignerRepository designerRepository,
            ApplicationUserDetailsService applicationUserDetailsService, PasswordEncoder passwordEncoder,
            CompromisedPasswordChecker compromisedPasswordChecker) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.designerRepository = designerRepository;
        this.applicationUserDetailsService = applicationUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    /**
     * User Login
     * 
     * *
     * <p>
     * Used to login a existing user on the system.
     * 
     * Generates a new token each time.
     * Is stateless, session ends when token is expired.
     * 
     * Will need to add logout function.
     * </p>
     * 
     * 
     * @param userLoginDTO user login request
     * @throws BadCredentialsException if the user login is invalid
     * @return a new jwttoken
     */
    public String login(UserLoginDTO userLoginDTO) {

        // Checks users login details are correct
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));

        ApplicationUserDetails applicationUserDetails = applicationUserDetailsService
                .loadUserByUsername(userLoginDTO.getUsername());

        // A new token will be generated
        return jwtService.generateJwtToken(applicationUserDetails);

    }

    /**
     * Register User (Only Designer)
     * 
     * 
     * <p>
     * Used to register a new user on the system and
     * automatically assigned a unique identifier.
     * 
     * Admin users will be manually inputted in the db.
     * </p>
     * 
     * @param userCreateDTO user register request
     * @throws UserExistsException if the user already exists
     * @return creates a new user
     */
    @Transactional
    public void registerDesigner(UserCreateDTO userCreateDTO) {

        // Checking if the user already exists - using email address and phone number
        String emailAddress = userCreateDTO.getEmailAddress();
        String phoneNumber = userCreateDTO.getPhoneNumber();

        Optional<User> existingUser = userRepository.findByEmailAddressOrPhoneNumber(emailAddress, phoneNumber);

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            if (user.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                throw new UserExistsException("mobile", userCreateDTO.getPhoneNumber());

            } else if (user.getEmailAddress().equalsIgnoreCase(emailAddress)) {
                throw new UserExistsException("email", userCreateDTO.getEmailAddress());
            }

        }

        // Checking if the password is compromised
        String password = userCreateDTO.getPassword();

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(password);

        if (decision.isCompromised()) {
            throw new CompromisedPasswordException("This password cannot be used and is compromised.");
        }

        // Established User's role (Designer)
        userCreateDTO.setRoles(Roles.DESIGNER);

        // Saves user in the user table
        User user = userMapper.toEntity(userCreateDTO);

        // Uses password encoder bean - hashes the password
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        // Saves userId in the designer table (One to One relationship)
        Designer designer = new Designer();
        designer.setUser(savedUser);
        designerRepository.save(designer);

    }

    /**
     * Retrieved the User's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the user object to be deleted
     * @throws UserNotFoundException if the user is not found
     * @return the user
     */
    public User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

}
