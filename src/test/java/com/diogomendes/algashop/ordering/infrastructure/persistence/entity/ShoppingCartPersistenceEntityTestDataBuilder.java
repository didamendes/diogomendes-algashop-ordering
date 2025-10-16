package com.diogomendes.algashop.ordering.infrastructure.persistence.entity;

import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder;

import java.math.BigDecimal;

import static com.diogomendes.algashop.ordering.domain.model.utility.IdGenerator.generateTimeBasedUUID;
import static com.diogomendes.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder.aCustomer;
import static java.time.OffsetDateTime.now;
import static java.util.Set.of;

public class ShoppingCartPersistenceEntityTestDataBuilder {

    private ShoppingCartPersistenceEntityTestDataBuilder() {
    }

    public static ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
        return ShoppingCartPersistenceEntity.builder()
                .id(generateTimeBasedUUID())
                .customer(aCustomer().build())
                .totalItems(3)
                .totalAmount(new BigDecimal(1250))
                .createdAt(now())
                .items(of
                        (
                                existingItem().build(),
                                existingItem().build()
                        )
                );
    }

    public static ShoppingCartItemPersistenceEntityBuilder existingItem() {
        return ShoppingCartItemPersistenceEntity.builder()
                .id(generateTimeBasedUUID())
                .price(new BigDecimal(500))
                .quantity(2)
                .totalAmount(new BigDecimal(1000))
                .name("Notebook")
                .productId(generateTimeBasedUUID());
    }

    public static ShoppingCartItemPersistenceEntityBuilder existingItemAlt() {
        return ShoppingCartItemPersistenceEntity.builder()
                .id(generateTimeBasedUUID())
                .price(new BigDecimal(250))
                .quantity(1)
                .totalAmount(new BigDecimal(250))
                .name("Mouse pad")
                .productId(generateTimeBasedUUID());
    }

}
