package com.interiordesignplanner.room;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Item name is required")
    @Size(min = 5, max = 30, message = "Item name must be between 5 and 30 characters")
    private String itemName;

    // Description of Item
    @NotNull(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;

    // Item Price
    @NotNull(message = "Price is required")
    @Digits(integer = 6, fraction = 2, message = "Price must be a valid amount")
    @Min(value = 0, message = "Price must not be negative")
    private BigDecimal price;

    // Item Quantity
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must not be lower than 1")
    private Integer quantity;

    // Item Dimensions
    @NotNull(message = "Dimensions is required")
    @Size(min = 5, max = 30, message = "Dimensions must be between 5 and 30 characters")
    private String dimensions;

    // Item Link
    @NotNull(message = "Link is required")
    private String link;

    // Item ordered?
    private boolean ordered;

}
