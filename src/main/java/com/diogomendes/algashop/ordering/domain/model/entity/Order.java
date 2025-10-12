package com.diogomendes.algashop.ordering.domain.model.entity;

import com.diogomendes.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.diogomendes.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.diogomendes.algashop.ordering.domain.model.exception.OrderInvalidShippingDeliveryDateException;
import com.diogomendes.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.exception.OrderCannotBePlacedException.*;
import static com.diogomendes.algashop.ordering.domain.model.exception.OrderCannotBePlacedException.noItems;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

public class Order implements AggregateRoot<OrderId> {

    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    private Billing billing;
    private Shipping shipping;

    private OrderStatus status;
    private PaymentMethod paymentMethod;

    private Set<OrderItem> items;

    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, CustomerId customerId, Money totalAmount, Quantity totalItems,
                 OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt,
                 OffsetDateTime readyAt, Billing billing, Shipping shipping,
                 OrderStatus status, PaymentMethod paymentMethod, Set<OrderItem> items, Long version) {
        this.setId(id);
        this.setVersion(version);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
                new OrderId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                DRAFT,
                null,
                new HashSet<>(),
                null
        );
    }

    public void addItem(Product product, Quantity quantity) {
        requireNonNull(product);
        requireNonNull(quantity);

        verifyIfChangeable();

        product.checkOutOfStock();

        OrderItem orderItem = OrderItem.brandNew()
                .orderId(this.id)
                .product(product)
                .quantity(quantity)
                .build();

        if (this.items == null) {
            this.items = new HashSet<>();
        }

        this.items.add(orderItem);

        this.recalculateTotals();
    }

    public void place() {
        this.verifyIfCanChangeToPlaced();
        this.setPlacedAt(OffsetDateTime.now());
        this.changeStatus(PLACED);
    }

    public void markAsPaid() {
        this.setPaidAt(OffsetDateTime.now());
        this.changeStatus(PAID);
    }

    public void markAsReady() {
        this.changeStatus(READY);
        this.setReadyAt(OffsetDateTime.now());
    }

    public void changePaymentMethod(PaymentMethod paymentMethod) {
        requireNonNull(paymentMethod);
        verifyIfChangeable();
        this.setPaymentMethod(paymentMethod);
    }

    public void changeBilling(Billing billing) {
        requireNonNull(billing);
        verifyIfChangeable();
        this.setBilling(billing);
    }

    public void changeShipping(Shipping newShipping) {
        requireNonNull(newShipping);
        verifyIfChangeable();

        if (newShipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id());
        }

        this.setShipping(newShipping);
        this.recalculateTotals();
    }

    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        requireNonNull(orderItemId);
        requireNonNull(quantity);

        verifyIfChangeable();

        OrderItem orderItem = this.findOrderItem(orderItemId);
        orderItem.changeQuantity(quantity);

        this.recalculateTotals();
    }

    public void removeItem(OrderItemId orderItemId) {
        requireNonNull(orderItemId);
        verifyIfChangeable();

        OrderItem orderItem = findOrderItem(orderItemId);
        this.items.remove(orderItem);

        recalculateTotals();
    }

    public void cancel() {
        this.changeStatus(CANCELED);
        this.setCanceledAt(OffsetDateTime.now());
    }

    public boolean isDraft() {
        return DRAFT.equals(this.status());
    }

    public boolean isPlaced() {
        return PLACED.equals(this.status());
    }

    public boolean isPaid() {
        return PAID.equals(this.status());
    }

    public boolean isReady() {
        return READY.equals(this.status());
    }

    public boolean isCanceled() {
        return CANCELED.equals(this.status());
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatus status() {
        return status;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Set<OrderItem> items() {
        return unmodifiableSet(this.items);
    }

    public Long version() {
        return version;
    }

    private void recalculateTotals() {
        BigDecimal totalItemsAmount = this.items().stream().map(i -> i.totalAmount().value())
                .reduce(ZERO, BigDecimal::add);

        Integer totalItemsQuantity = this.items().stream().map(i -> i.quantity().value())
                .reduce(0, Integer::sum);

        BigDecimal shippingCost;
        if (this.shipping() == null) {
            shippingCost = ZERO;
        } else {
            shippingCost = this.shipping().cost().value();
        }

        BigDecimal totalAmount = totalItemsAmount.add(shippingCost);

        this.setTotalAmount(new Money(totalAmount));
        this.setTotalItems(new Quantity(totalItemsQuantity));
    }

    private void changeStatus(OrderStatus newStatus) {
        requireNonNull(newStatus);
        if (this.status().canNotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id(), this.status(), newStatus);
        }

        this.setStatus(newStatus);
    }

    private void verifyIfCanChangeToPlaced() {
        if (this.shipping() == null) {
            throw noShippingInfo(this.id());
        }
        if (this.billing() == null) {
            throw noBillingInfo(this.id());
        }
        if (this.paymentMethod() == null) {
            throw noPaymentMethod(this.id());
        }
        if (this.items() == null || this.items().isEmpty()) {
            throw noItems(this.id());
        }
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        requireNonNull(orderItemId);
        return this.items().stream()
                .filter(i -> i.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id(), orderItemId));
    }

    private void verifyIfChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.id(), this.status());
        }
    }

    private void setId(OrderId id) {
        requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatus status) {
        requireNonNull(status);
        this.status = status;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    private void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setItems(Set<OrderItem> items) {
        requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
