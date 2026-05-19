package com.interiordesignplanner.room;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating and updating a shopping list item")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Item {

    @Schema(description = "Item image url", example = "https://cdn.sklum.com/uk/wk/5304236/wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.jpg?cf-resize=gallery")
    private String imageUrl;

    @Schema(description = "Item name", example = "Isasia table lamp")
    @NotNull(message = "Item name is required")
    @Size(min = 5, max = 30, message = "Item name must be between 5 and 30 characters")
    private String itemName;

    @Schema(description = "Item description", example = "Wireless LED outdoor table lamp in iron and natural stone")
    @NotNull(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;

    @Schema(description = "Item price", example = "40.00")
    @NotNull(message = "Price is required")
    @Digits(integer = 6, fraction = 2, message = "Price must be a valid amount")
    @Min(value = 0, message = "Price must not be negative")
    private BigDecimal price;

    @Schema(description = "Item quantity", example = "1")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must not be lower than 1")
    private Integer quantity;

    @Schema(description = "Item dimensions", example = "height: 15cm, diameter: 12 cm")
    @NotNull(message = "Dimensions is required")
    @Size(min = 5, max = 30, message = "Dimensions must be between 5 and 30 characters")
    private String dimensions;

    @Schema(description = "Link", example = "https://www.sklum.com/uk/buy-outdoor-table-lamps/220716-wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.html")
    @NotNull(message = "Link is required")
    private String link;

    @Schema(description = "Item ordered?", example = "false")
    private boolean ordered;

}
