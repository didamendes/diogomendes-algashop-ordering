package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.diogomendes.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static com.diogomendes.algashop.ordering.domain.model.commons.Money.ZERO;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.aBilling;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.aShipping;
import static com.diogomendes.algashop.ordering.domain.model.order.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProductUnavailable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    private BuyNowService buyNowService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup() {
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                new LoyaltyPoints(100),
                2L,
                new LoyaltyPoints(2000)
        );
        buyNowService = new BuyNowService(specification);
    }

    @Test
    void givenValidProductAndDetails_whenBuyNow_shouldReturnPlacedOrder() {
        Product product = aProduct().build();
        Customer customer = existingCustomer().build();
        Billing billing = aBilling();
        var shipping = aShipping();
        Quantity quantity = new Quantity(3);
        PaymentMethod paymentMethod = CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customer.id());
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
        Customer customer = existingCustomer().build();
        Billing billingInfo = aBilling();
        Shipping shippingInfo = aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod));
    }

    @Test
    void givenInvalidQuantity_whenBuyNow_shouldThrowIllegalArgumentException() {
        Product product = aProduct().build();
        Customer customer = existingCustomer().build();
        Billing billingInfo = aBilling();
        Shipping shippingInfo = aShipping();
        Quantity quantity = new Quantity(0);
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod));
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_shouldReturnPlacedOrderWithFreeShipping() {
        when(orders.salesQuantityByCustomerInYear(
                any(CustomerId.class),
                any(Year.class)
        )).thenReturn(2L);

        Product product = aProduct().build();
        Customer customer = existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        Billing billing = aBilling();
        var shipping = aShipping();
        Quantity quantity = new Quantity(3);
        PaymentMethod paymentMethod = CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customer.id());
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(ZERO).build());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        Money expectedTotalAmount = product.price().multiply(quantity);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);
    }

}