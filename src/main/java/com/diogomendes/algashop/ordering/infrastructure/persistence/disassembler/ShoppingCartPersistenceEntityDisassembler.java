package com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler;

import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import com.diogomendes.algashop.ordering.domain.model.valueobject.ProductName;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

    public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity source) {
        return ShoppingCart.existing()
                .id(new ShoppingCartId(source.getId()))
                .customerId(new CustomerId(source.getCustomerId()))
                .totalAmount(new Money(source.getTotalAmount()))
                .createdAt(source.getCreatedAt())
                .items(toItemsDomainEntities(source.getItems()))
                .totalItems(new Quantity(source.getTotalItems()))
                .build();
    }

    private Set<ShoppingCartItem> toItemsDomainEntities(Set<ShoppingCartItemPersistenceEntity> sources) {
        return sources.stream()
                .map(this::toItemEntity)
                .collect(Collectors.toSet());
    }

    private ShoppingCartItem toItemEntity(ShoppingCartItemPersistenceEntity source) {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId(source.getId()))
                .shoppingCartId(new ShoppingCartId(source.getShoppingCardId()))
                .productId(new ProductId(source.getProductId()))
                .productName(new ProductName(source.getName()))
                .price(new Money(source.getPrice()))
                .quantity(new Quantity(source.getQuantity()))
                .available(source.getAvailable())
                .totalAmount(new Money(source.getTotalAmount()))
                .build();
    }

}
