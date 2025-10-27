package com.diogomendes.algashop.ordering.domain.model.customer;

import java.time.OffsetDateTime;

public record CustomerArchivedEvent(
        CustomerId customerId,
        OffsetDateTime archivedAt
) {
}
