package com.diogomendes.algashop.ordering.domain.valueobject.id;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.utility.IdGenerator.generateTimeBaseUUID;
import static java.util.Objects.requireNonNull;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        requireNonNull(value);
    }

    public ShoppingCartId() {
        this(generateTimeBaseUUID());
    }

    public ShoppingCartId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
