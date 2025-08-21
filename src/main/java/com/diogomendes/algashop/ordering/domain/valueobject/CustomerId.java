package com.diogomendes.algashop.ordering.domain.valueobject;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.utility.IdGenerator.generateTimeBaseUUID;
import static java.util.Objects.requireNonNull;

public record CustomerId(
        UUID value
) {

    public CustomerId() {
        this(generateTimeBaseUUID());
    }
    public CustomerId(UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
