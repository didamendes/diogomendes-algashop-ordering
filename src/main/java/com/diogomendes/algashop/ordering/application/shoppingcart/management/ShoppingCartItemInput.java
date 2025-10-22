package com.diogomendes.algashop.ordering.application.shoppingcart.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartItemInput {
    private UUID productId;
    private Integer quantity;
    private UUID shoppingCartId;
}
