package com.diogomendes.algashop.ordering.domain.model.valueobject.id;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTimeBasedUUID;
import static java.util.Objects.requireNonNull;

public record CustomerId(
        UUID value
) {

    public CustomerId() {
        this(generateTimeBasedUUID());
    }
    public CustomerId(UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
