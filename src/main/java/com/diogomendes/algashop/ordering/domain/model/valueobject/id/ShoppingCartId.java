package com.diogomendes.algashop.ordering.domain.model.valueobject.id;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTimeBasedUUID;
import static java.util.Objects.requireNonNull;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        requireNonNull(value);
    }

    public ShoppingCartId() {
        this(generateTimeBasedUUID());
    }

    public ShoppingCartId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
