package com.diogomendes.algashop.ordering.domain.model.product;

import com.diogomendes.algashop.ordering.domain.model.DomainException;

import static com.diogomendes.algashop.ordering.domain.model.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(ProductId id) {
        super(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, id));
    }

}
