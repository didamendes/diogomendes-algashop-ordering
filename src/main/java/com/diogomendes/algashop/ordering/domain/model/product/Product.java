package com.diogomendes.algashop.ordering.domain.model.product;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Product(
        ProductId id,
        ProductName name,
        Money price,
        Boolean inStock
) {

    public Product {
        requireNonNull(id);
        requireNonNull(name);
        requireNonNull(price);
        requireNonNull(inStock);
    }

    public void checkOutOfStock() {
        if (isOutOfStock()) {
            throw new ProductOutOfStockException(this.id());
        }
    }

    private boolean isOutOfStock() {
        return !inStock;
    }
}
