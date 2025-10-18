package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.Customer;
import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.utility.DomainService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Billing;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Shipping;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static com.diogomendes.algashop.ordering.domain.model.entity.Order.draft;

@DomainService
public class BuyNowService {

    public Order buyNow(Product product,
                        CustomerId customer,
                        Billing billing,
                        Shipping shipping,
                        Quantity quantity,
                        PaymentMethod paymentMethod) {
        product.checkOutOfStock();

        Order order = draft(customer);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);
        order.place();

        return order;
    }

}
