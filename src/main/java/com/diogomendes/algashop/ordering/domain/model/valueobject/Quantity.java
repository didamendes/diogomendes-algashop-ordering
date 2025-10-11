package com.diogomendes.algashop.ordering.domain.model.valueobject;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

public record Quantity(Integer value) implements Serializable, Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        requireNonNull(value);
        if (value < 0) {
            throw new IllegalArgumentException();
        }
    }

    public Quantity add(Quantity quantity) {
        requireNonNull(quantity);
        return new Quantity(this.value + quantity.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value.compareTo(o.value);
    }
}
