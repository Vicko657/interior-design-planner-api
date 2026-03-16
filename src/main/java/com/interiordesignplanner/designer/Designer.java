package com.interiordesignplanner.designer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interiordesignplanner.AbstractEntity;
import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.client.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "designers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Designer extends AbstractEntity {

    // Designer's profileImage
    private String profileImage;

    // Designer's bio
    private String bio;

    // Designer's experience
    private Integer experience;

    // Designer's location
    private String location;

    // Creates One to One Bidirectional relationship with the user entity
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Creates One to Many Bidirectional relationship with the client entity
    @OneToMany(mappedBy = "designer")
    @JsonManagedReference
    private List<Client> clients = new ArrayList<>();

}
