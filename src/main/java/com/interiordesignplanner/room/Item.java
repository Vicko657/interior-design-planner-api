package com.interiordesignplanner.room;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
// Constructs a Room Shopping List
public class Item {

    // URL of Item Image
    private String imageUrl;

    // Name of Item
    private String itemName;

    // Description of Item
    private String description;

    // Item Price
    private double price;

    // Item Quantity
    private int quantity;

    // Item Dimensions
    private String dimensions;

    // Item Link
    private String link;

    // Item ordered?
    private boolean ordered;

}
