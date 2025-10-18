package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

import static com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder.aBilling;
import static com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder.aShipping;
import static com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProductUnavailable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class BuyNowServiceTest {

    private final BuyNowService buyNowService = new BuyNowService();

    @Test
    void givenValidProductAndDetails_whenBuyNow_shouldReturnPlacedOrder() {
        Product product = aProduct().build();
        CustomerId customerId = new CustomerId();
        Billing billing = aBilling();
        var shipping = aShipping();
        Quantity quantity = new Quantity(3);
        PaymentMethod paymentMethod = CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customerId);
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.shipping()).isEqualTo(shipping);
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        Money expectedTotalAmount = product.price().multiply(quantity).add(shipping.cost());
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);
    }

    @Test
    void givenOutOfStockProduct_whenBuyNow_shouldThrowProductOutOfStockException() {
        Product product = aProductUnavailable().build();
        CustomerId customerId = new CustomerId();
        Billing billingInfo = aBilling();
        Shipping shippingInfo = aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customerId, billingInfo, shippingInfo, quantity, paymentMethod));
    }

    @Test
    void givenInvalidQuantity_whenBuyNow_shouldThrowIllegalArgumentException() {
        Product product = aProduct().build();
        CustomerId customerId = new CustomerId();
        Billing billingInfo = aBilling();
        Shipping shippingInfo = aShipping();
        Quantity quantity = new Quantity(0);
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customerId, billingInfo, shippingInfo, quantity, paymentMethod));
    }

}