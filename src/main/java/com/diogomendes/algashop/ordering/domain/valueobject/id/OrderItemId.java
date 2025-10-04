package com.diogomendes.algashop.ordering.domain.valueobject.id;

import com.diogomendes.algashop.ordering.domain.utility.IdGenerator;
import io.hypersistence.tsid.TSID;

import static java.util.Objects.requireNonNull;

public record OrderItemId(TSID value) {

    public OrderItemId {
        requireNonNull(value);
    }

    public OrderItemId() {
        this(IdGenerator.generateTSID());
    }

    public OrderItemId(Long value) {
        this(TSID.from(value));
    }

    public OrderItemId(String value) {
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
