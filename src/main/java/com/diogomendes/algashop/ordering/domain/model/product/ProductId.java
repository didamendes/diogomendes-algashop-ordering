package com.diogomendes.algashop.ordering.domain.model.product;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.IdGenerator.generateTimeBasedUUID;
import static java.util.Objects.requireNonNull;

public record ProductId(
        UUID value
) {

    public ProductId() {
        this(generateTimeBasedUUID());
    }

    public ProductId(UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
