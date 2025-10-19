package com.diogomendes.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity,
                                               ShoppingCart shoppingCart) {
        persistenceEntity.setId(shoppingCart.id().value());
        persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        persistenceEntity.setCreatedAt(shoppingCart.createdAt());
        persistenceEntity.replaceItems(toOrderItemsEntities(shoppingCart.items()));

        persistenceEntity.setCustomer(customerPersistenceEntityRepository
                .getReferenceById(shoppingCart.customerId().value()));

        return persistenceEntity;
    }

    public Set<ShoppingCartItemPersistenceEntity> toOrderItemsEntities(Set<ShoppingCartItem> source) {
        return source.stream()
                .map(i -> this.merge(new ShoppingCartItemPersistenceEntity(), i))
                .collect(Collectors.toSet());
    }

    private ShoppingCartItemPersistenceEntity merge(ShoppingCartItemPersistenceEntity persistenceEntity,
                                                    ShoppingCartItem shoppingCartItem) {
        persistenceEntity.setId(shoppingCartItem.id().value());
        persistenceEntity.setProductId(shoppingCartItem.productId().value());
        persistenceEntity.setName(shoppingCartItem.productName().value());
        persistenceEntity.setPrice(shoppingCartItem.price().value());
        persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        persistenceEntity.setAvailable(shoppingCartItem.isAvailable());
        persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());

        return persistenceEntity;
    }

    private ShoppingCartItemPersistenceEntity toOrderItemsEntities(ShoppingCartItem source) {
        return ShoppingCartItemPersistenceEntity.builder()
                .id(source.id().value())
                .shoppingCart(ShoppingCartPersistenceEntity.builder().id(source.shoppingCartId().value()).build())
                .productId(source.productId().value())
                .name(source.productName().value())
                .price(source.price().value())
                .quantity(source.quantity().value())
                .available(source.isAvailable())
                .totalAmount(source.totalAmount().value())
                .build();
    }

}
