package com.diogomendes.algashop.ordering.domain.valueobject.id;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.utility.IdGenerator.generateTimeBaseUUID;
import static java.util.Objects.requireNonNull;

public record ProductId(
        UUID value
) {

    public ProductId() {
        this(generateTimeBaseUUID());
    }

    public ProductId(UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
