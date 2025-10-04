package com.diogomendes.algashop.ordering.domain.valueobject.id;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.utility.IdGenerator.generateTimeBaseUUID;
import static java.util.Objects.requireNonNull;

public record ShoppingCartItemId(UUID value) {

    public ShoppingCartItemId {
        requireNonNull(value);
    }

    public ShoppingCartItemId() {
        this(generateTimeBaseUUID());
    }

    public ShoppingCartItemId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
