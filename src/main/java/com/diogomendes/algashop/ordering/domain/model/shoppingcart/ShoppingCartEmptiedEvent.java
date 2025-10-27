package com.diogomendes.algashop.ordering.domain.model.shoppingcart;

import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record ShoppingCartEmptiedEvent(
        ShoppingCartId shoppingCartId,
        CustomerId customerId,
        OffsetDateTime emptiedAt
) {
}
