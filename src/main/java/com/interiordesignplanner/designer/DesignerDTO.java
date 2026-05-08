package com.interiordesignplanner.designer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignerDTO {

    // Designer's id
    private Long id;

    // Designer's profileImage
    private String profileImage;

    // Designer's bio
    private String bio;

    // Designer's experience
    private Integer experience;

    // Designer's location
    private String location;

    // How many clients the designer has
    private Long totalClients;

}
