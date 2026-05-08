package com.interiordesignplanner.designer;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DesignerCreateDTO {

    // Designer's profileImage
    private String profileImage;

    // Designer's bio
    @Size(min = 5, max = 200, message = "Bio must be between 5 and 200 characters")
    private String bio;

    // Designer's experience
    private Integer experience;

    // Designer's location
    @Size(min = 3, max = 15, message = "Location must be between 3 and 15 characters")
    private String location;

}
