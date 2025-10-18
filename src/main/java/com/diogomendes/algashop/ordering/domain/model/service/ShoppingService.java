package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.diogomendes.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.repository.Customers;
import com.diogomendes.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.diogomendes.algashop.ordering.domain.model.utility.DomainService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(CustomerId customerId) {
        if (!customers.exists(customerId)) {
            throw new CustomerNotFoundException();
        }

        if (shoppingCarts.ofCustomer(customerId).isPresent()) {
            throw new CustomerAlreadyHaveShoppingCartException();
        }

        return ShoppingCart.startShopping(customerId);
    }

}
