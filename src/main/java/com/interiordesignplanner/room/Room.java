package com.interiordesignplanner.room;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.interiordesignplanner.AbstractEntity;
import com.interiordesignplanner.project.Project;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Models a room within a project, like a bedroom, living room or bathroom.
 * The room entity helps track dimensions,specifications, design elements
 * and styling. One room belongs to one project and is extending the
 * AbstractEntity class, which provides their unique identifier and
 * timestamps for creation and updates to their data.
 */

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room extends AbstractEntity {

    // Foreign key to Project entity, one to one bidirectional relationship.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @JsonBackReference
    private Project project;

    // Categories the specific type of room
    @Enumerated(EnumType.STRING)
    private RoomType type;

    // Dimemsions of the room
    private Double length;
    private Double height;
    private Double width;

    private String unit;

    // Checklist of tasks specific to the room
    @ElementCollection
    @OrderColumn(name = "checklist_key")
    private List<Task> checklist = new ArrayList<>();

    // Inventory list for the room
    @ElementCollection
    private List<Item> inventory = new ArrayList<>();

}
