package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderItemTest {

    @Test
    public void shouldGenerateBrandNewOrderItem() {
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity quantity = new Quantity(1);
        OrderId orderId = new OrderId();

        OrderItem orderItem = OrderItem.brandNew()
                .product(product)
                .quantity(quantity)
                .orderId(orderId)
                .build();

        assertWith(orderItem,
                o-> assertThat(o.id()).isNotNull(),
                o-> assertThat(o.productId()).isEqualTo(product.id()),
                o-> assertThat(o.productName()).isEqualTo(product.name()),
                o-> assertThat(o.price()).isEqualTo(product.price()),
                o-> assertThat(o.quantity()).isEqualTo(quantity),
                o-> assertThat(o.orderId()).isEqualTo(orderId)
        );
    }

}