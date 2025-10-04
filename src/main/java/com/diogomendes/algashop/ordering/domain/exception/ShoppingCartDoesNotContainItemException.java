package com.diogomendes.algashop.ordering.domain.exception;

import com.diogomendes.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.diogomendes.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;

import static com.diogomendes.algashop.ordering.domain.exception.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainItemException extends DomainException {
    public ShoppingCartDoesNotContainItemException(ShoppingCartId id, ShoppingCartItemId shoppingCartItemId) {
        super(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, id, shoppingCartItemId));
    }
}
