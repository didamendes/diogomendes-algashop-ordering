package com.diogomendes.algashop.ordering.domain.model.repository;

import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import java.util.Optional;

public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
    Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}
