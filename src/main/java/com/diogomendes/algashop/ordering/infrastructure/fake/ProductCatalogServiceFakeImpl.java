package com.diogomendes.algashop.ordering.infrastructure.fake;

import com.diogomendes.algashop.ordering.domain.model.service.ProductCatalogService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.ProductName;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

public class ProductCatalogServiceFakeImpl implements ProductCatalogService {
    @Override
    public Optional<Product> ofId(ProductId productId) {
        Product product = Product.builder().id(productId)
                .inStock(true)
                .name(new ProductName("Notebook"))
                .price(new Money("3000"))
                .build();
        return Optional.of(product);
    }
}
