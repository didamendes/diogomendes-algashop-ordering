package com.diogomendes.algashop.ordering.domain.model.valueobject.id;

import io.hypersistence.tsid.TSID;

import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTSID;
import static java.util.Objects.requireNonNull;

public record OrderId(TSID value) {

    public OrderId {
        requireNonNull(value);
    }

    public OrderId() {
        this(generateTSID());
    }

    public OrderId(Long value) {
        this(TSID.from(value));
    }

    public OrderId(String value) {
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
