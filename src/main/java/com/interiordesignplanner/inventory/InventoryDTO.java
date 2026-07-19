package com.interiordesignplanner.inventory;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response body for a inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {

    @Schema(description = "Item image url", example = "https://cdn.sklum.com/uk/wk/5304236/wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.jpg?cf-resize=gallery")
    private String imageUrl;

    @Schema(description = "Item name", example = "Isasia table lamp")
    private String itemName;

    @Schema(description = "Item description", example = "Wireless LED outdoor table lamp in iron and natural stone")
    private String description;

    @Schema(description = "Item price", example = "40.00")
    private BigDecimal price;

    @Schema(description = "Item quantity", example = "1")
    private Integer quantity;

    @Schema(description = "Item dimensions", example = "height: 15cm, diameter: 12 cm")
    private String dimensions;

    @Schema(description = "Link", example = "https://www.sklum.com/uk/buy-outdoor-table-lamps/220716-wireless-led-outdoor-table-lamp-in-iron-and-natural-stone-isasia.html")
    private String link;

    @Schema(description = "Item ordered?", example = "false")
    private boolean isOrdered;

    @Schema(description = "Project name", example = "Luxury Master Bedroom")
    private String projectName;

    @Schema(description = "Item total price", example = "40.00")
    private BigDecimal totalPrice;

}
