package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.commons.Document;
import com.diogomendes.algashop.ordering.domain.model.commons.FullName;
import com.diogomendes.algashop.ordering.domain.model.commons.Phone;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Recipient(FullName fullName, Document document, Phone phone) {

    public Recipient {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
    }
}
