package com.diogomendes.algashop.ordering.domain.model.exception;

import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

import static com.diogomendes.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
    public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId id, ProductId productId) {
        super(String.format(ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, id, productId));
    }
}
