package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

public interface ProductCatalogService {
    Optional<Product> ofId(ProductId productId);
}
