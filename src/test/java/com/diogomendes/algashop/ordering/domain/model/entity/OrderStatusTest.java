package com.diogomendes.algashop.ordering.domain.model.entity;

import org.junit.jupiter.api.Test;

import static com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus.CANCELED;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    public void canChangeTo() {
        assertThat(DRAFT.canChangeTo(PLACED)).isTrue();
        assertThat(DRAFT.canChangeTo(CANCELED)).isTrue();
        assertThat(PAID.canChangeTo(DRAFT)).isFalse();
    }

    @Test
    public void canNotChangeTo() {
        assertThat(PLACED.canNotChangeTo(DRAFT)).isTrue();
    }

}