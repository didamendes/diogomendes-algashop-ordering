package com.diogomendes.algashop.ordering.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Objects.requireNonNull;

public record Money(BigDecimal value) implements Comparable<Money> {

    private static final RoundingMode roundingMode = HALF_EVEN;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money(BigDecimal value) {
        requireNonNull(value);
        this.value = value.setScale(2, roundingMode);
        if (this.value.signum() == -1) {
            throw new IllegalArgumentException();
        }
    }

    public Money multiply(Quantity quantity) {
        requireNonNull(quantity);
        if (quantity.value() < 1) {
            throw new IllegalArgumentException();
        }
        return new Money(this.value.multiply(BigDecimal.valueOf(quantity.value())));
    }

    public Money add(Money money) {
        requireNonNull(money);
        return new Money(this.value.add(money.value));
    }

    public Money divide(Money money) {
        return new Money(this.value.divide(money.value, roundingMode));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(Money o) {
        return this.value.compareTo(o.value);
    }
}
