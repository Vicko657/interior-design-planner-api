package com.interiordesignplanner.designer;

import org.springframework.stereotype.Service;

import com.interiordesignplanner.exceptions.UserNotFoundException;

@Service
public class DesignerService {
    private final DesignerRepository designerRepository;

    public DesignerService(DesignerRepository designerRepository) {
        this.designerRepository = designerRepository;
    }

    /**
     * Retrieved the Designer's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the user object to be deleted
     * @throws UserNotFoundException if the user is not found
     * @return the user
     */
    public Designer findDesigner(Long userId) {
        return designerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

}
