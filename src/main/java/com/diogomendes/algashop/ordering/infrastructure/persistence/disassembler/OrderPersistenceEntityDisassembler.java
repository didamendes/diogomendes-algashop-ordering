package com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderItem;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus;
import com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlacedAt())
                .paidAt(persistenceEntity.getPaidAt())
                .canceledAt(persistenceEntity.getCanceledAt())
                .readyAt(persistenceEntity.getReadyAt())
                .items(toDomainEntity(persistenceEntity.getItems()))
                .version(persistenceEntity.getVersion())
                .build();
    }

    private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
        return items.stream().map(this::toDomainEntity).collect(Collectors.toSet());
    }

    private OrderItem toDomainEntity(OrderItemPersistenceEntity persistenceEntity) {
        return OrderItem.existing()
                .id(new OrderItemId(persistenceEntity.getId()))
                .orderId(new OrderId(persistenceEntity.getOrderId()))
                .productId(new ProductId(persistenceEntity.getProductId()))
                .productName(new ProductName(persistenceEntity.getProductName()))
                .price(new Money(persistenceEntity.getPrice()))
                .quantity(new Quantity(persistenceEntity.getQuantity()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .build();
    }

    private Shipping toShippingValueObject(ShippingEmbeddable shippingEmbeddable) {
        Recipient recipient = toRecipientValueObject(shippingEmbeddable.getRecipient());
        return Shipping.builder()
                .cost(new Money(shippingEmbeddable.getCost()))
                .expectedDate(shippingEmbeddable.getExpectedDate())
                .recipient(recipient)
                .address(toAddressValueObject(shippingEmbeddable.getAddress()))
                .build();
    }

    private Billing toBillingValueObject(BillingEmbeddable billingEmbeddable) {
        return Billing.builder()
                .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(new Document(billingEmbeddable.getDocument()))
                .phone(new Phone(billingEmbeddable.getPhone()))
                .address(toAddressValueObject(billingEmbeddable.getAddress()))
                .build();
    }

    private Recipient toRecipientValueObject(RecipientEmbeddable recipientEmbeddable) {
        return Recipient.builder()
                .fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .document(new Document(recipientEmbeddable.getDocument()))
                .phone(new Phone(recipientEmbeddable.getPhone()))
                .build();
    }

    private Address toAddressValueObject(AddressEmbeddable address) {
        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(new ZipCode(address.getZipCode()))
                .build();
    }

}
