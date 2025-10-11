package com.diogomendes.algashop.ordering.domain.model.factory;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Billing;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Shipping;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static java.util.Objects.requireNonNull;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(
            CustomerId customerId,
            Shipping shipping,
            Billing billing,
            PaymentMethod paymentMethod,
            Product product,
            Quantity productQuantity
    ) {
        requireNonNull(customerId);
        requireNonNull(shipping);
        requireNonNull(billing);
        requireNonNull(paymentMethod);
        requireNonNull(product);
        requireNonNull(productQuantity);

        Order order = Order.draft(customerId);

        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, productQuantity);

        return order;
    }

}
