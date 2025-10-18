package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.diogomendes.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.diogomendes.algashop.ordering.domain.model.utility.DomainService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Billing;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Shipping;

import java.util.Set;

import static com.diogomendes.algashop.ordering.domain.model.entity.Order.draft;

@DomainService
public class CheckoutService {

    public Order checkout(ShoppingCart shoppingCart,
                          Billing billing,
                          Shipping shipping,
                          PaymentMethod paymentMethod) {
        if (shoppingCart.isEmpty()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        if (shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        Set<ShoppingCartItem> items = shoppingCart.items();

        Order order = draft(shoppingCart.customerId());
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);

        for (ShoppingCartItem item : items) {
            order.addItem(new Product(item.productId(), item.productName(),
                    item.price(), item.isAvailable()), item.quantity());
        }

        order.place();
        shoppingCart.empty();

        return order;
    }

}
