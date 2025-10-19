package com.diogomendes.algashop.ordering.infrastructure.persistence.entity;

import com.diogomendes.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity.CustomerPersistenceEntityBuilder;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static java.time.LocalDate.of;
import static java.time.OffsetDateTime.now;

public class CustomerPersistenceEntityTestDataBuilder {

    public CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntityBuilder aCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .registeredAt(now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .firstName("John")
                .lastName("Doe")
                .birthDate(of(1991, 7, 5))
                .email("johndoe@email.com")
                .phone("478-256-2604")
                .document("255-08-0578")
                .loyaltyPoints(0)
                .address(AddressEmbeddable.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode("123456")
                        .complement("Apt. 114")
                        .build());
    }

}
