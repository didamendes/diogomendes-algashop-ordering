package com.diogomendes.algashop.ordering.domain.model.exception;

import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;

import static com.diogomendes.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(ProductId id) {
        super(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, id));
    }

}
