package com.diogomendes.algashop.ordering.domain.model.shoppingcart;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {
    void adjustPrice(ProductId productId, Money updatedPrice);
    void changeAvailability(ProductId productId, boolean available);
}
