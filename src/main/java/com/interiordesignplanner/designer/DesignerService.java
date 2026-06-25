package com.interiordesignplanner.designer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.exceptions.UserNotFoundException;
import com.interiordesignplanner.mapper.DesignerMapper;

@Service
public class DesignerService {
    private final UserRepository userRepository;
    private final DesignerMapper designerMapper;
    private final DesignerRepository designerRepository;

    public DesignerService(DesignerRepository designerRepository, DesignerMapper designerMapper,
            UserRepository userRepository) {
        this.designerRepository = designerRepository;
        this.designerMapper = designerMapper;
        this.userRepository = userRepository;
    }

    /**
     * Return the Designer's profile
     * 
     * Reduces code repetition
     * 
     * @param username retrieves the current auth user
     * @throws UserNotFoundException if the user is not found
     * @return the designer's profile
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('DESIGNER')")
    public DesignerProfileDTO getProfile(String username) {
        User authUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));

        Designer authDesigner = findDesigner(authUser.getId());

        return designerMapper.toDto(authDesigner);
    }

    /**
     * Updates auth designer details on the system.
     * 
     * <p>
     * Use this method to modify details of a authenticated designer on
     * the system.
     * </p>
     * 
     * @param DesignerProfileUpdateDTO the designer object is updated
     * @param username                 retrieves the current auth user
     * @throws UserNotFoundException if the designer is not found
     * @return the updated designer object
     */
    @PreAuthorize("hasRole('DESIGNER')")
    @Transactional
    public DesignerProfileDTO updateProfile(DesignerProfileUpdateDTO designerProfileUpdateDTO,
            String username) {

        User authUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));

        Designer authDesigner = findDesigner(authUser.getId());

        designerMapper.updateProfile(designerProfileUpdateDTO, authDesigner);

        userRepository.save(authUser);
        designerRepository.save(authDesigner);

        return designerMapper.toDto(authDesigner);
    }

    /**
     * Retrieved the Designer's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the user object
     * @throws UserNotFoundException if the user is not found
     * @return the user
     */
    public Designer findDesigner(Long userId) {
        return designerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

}
