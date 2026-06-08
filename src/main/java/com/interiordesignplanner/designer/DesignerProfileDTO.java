package com.interiordesignplanner.designer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignerProfileDTO {

    // Designer's id
    private Long id;

    // Designer's name
    private String name;

    // Designer's email address
    private String emailAddress;

    // Designer's phone number
    private String phoneNumber;

    // Designer's profileImage
    private String profileImage;

    // Designer's bio
    private String bio;

    // Designer's experience
    private Integer experience;

    // Designer's location
    private String location;

}
