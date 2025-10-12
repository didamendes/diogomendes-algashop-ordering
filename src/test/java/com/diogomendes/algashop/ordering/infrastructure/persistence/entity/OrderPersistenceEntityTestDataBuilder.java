package com.diogomendes.algashop.ordering.infrastructure.persistence.entity;

import com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTSID;
import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTimeBasedUUID;

public class OrderPersistenceEntityTestDataBuilder {

    public OrderPersistenceEntityTestDataBuilder() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(generateTSID().toLong())
                .customerId(generateTimeBasedUUID())
                .totalItems(3)
                .totalAmount(new BigDecimal(1250))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .items(Set.of(
                        existingItem().build(),
                        existingItemAlt().build())
                );
    }

    public static OrderItemPersistenceEntityBuilder existingItem() {
        return OrderItemPersistenceEntity.builder()
                .id(generateTSID().toLong())
                .productId(generateTimeBasedUUID())
                .productName("Notebook")
                .price(new BigDecimal(500))
                .quantity(2)
                .totalAmount(new BigDecimal(1000));
    }

    public static OrderItemPersistenceEntityBuilder existingItemAlt() {
        return OrderItemPersistenceEntity.builder()
                .id(generateTSID().toLong())
                .productId(generateTimeBasedUUID())
                .productName("Mouse pad")
                .price(new BigDecimal(250))
                .quantity(1)
                .totalAmount(new BigDecimal(250));
    }

}
