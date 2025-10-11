package com.diogomendes.algashop.ordering.domain.model.exception;

import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import static com.diogomendes.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

public class ShoppingCartDoesNotContainProductException extends DomainException {
    public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
        super(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, id, productId));
    }
}
