package com.diogomendes.algashop.ordering.domain.model.shoppingcart;

import com.diogomendes.algashop.ordering.domain.model.DomainException;
import com.diogomendes.algashop.ordering.domain.model.product.ProductId;

import static com.diogomendes.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

public class ShoppingCartDoesNotContainProductException extends DomainException {
    public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
        super(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, id, productId));
    }
}
