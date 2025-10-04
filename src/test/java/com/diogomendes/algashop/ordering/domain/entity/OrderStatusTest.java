package com.diogomendes.algashop.ordering.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.diogomendes.algashop.ordering.domain.entity.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.entity.OrderStatus.CANCELED;
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