package com.diogomendes.algashop.ordering.domain.model.shoppingcart;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.IdGenerator.generateTimeBasedUUID;
import static java.util.Objects.requireNonNull;

public record ShoppingCartItemId(UUID value) {

    public ShoppingCartItemId {
        requireNonNull(value);
    }

    public ShoppingCartItemId() {
        this(generateTimeBasedUUID());
    }

    public ShoppingCartItemId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
