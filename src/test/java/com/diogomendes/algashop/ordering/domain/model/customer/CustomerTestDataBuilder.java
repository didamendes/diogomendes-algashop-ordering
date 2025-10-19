package com.diogomendes.algashop.ordering.domain.model.customer;

import com.diogomendes.algashop.ordering.domain.model.commons.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerTestDataBuilder {

    public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId();

    private CustomerTestDataBuilder() {

    }

    public static Customer.BrandNewCustomerBuilder brandNewCustomer() {
        return Customer.brandNew()
        .fullName(new FullName("John", "Doe"))
        .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
        .email(new Email("john.doe@gmail.com"))
        .phone(new Phone("478-256-2504"))
        .document(new Document("255-08-0578"))
        .promotionNotificationsAllowed(false)
        .address(Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("Noth Ville")
            .city("York")
            .state("South California")
            .zipCode(new ZipCode("12345"))
            .complement("Apt. 114")
            .build());
    }

    public static Customer.ExistingCustomerBuilder existingCustomer() {
        return Customer.existing()
            .id(DEFAULT_CUSTOMER_ID)
            .registeredAt(OffsetDateTime.now())
            .promotionNotificationsAllowed(true)
            .archived(false)
            .archivedAt(null)
            .fullName(new FullName("John","Doe"))
            .birthDate(new BirthDate(LocalDate.of(1991, 7,5)))
            .email(new Email("johndoe@email.com"))
            .phone(new Phone("478-256-2604"))
            .document(new Document("255-08-0578"))
            .promotionNotificationsAllowed(true)
            .loyaltyPoints(new LoyaltyPoints(0))
            .address(Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement("Apt. 114")
                .build())
            ;
    }

    public static Customer.ExistingCustomerBuilder existingCustomerBuilder() {
        return Customer.existing()
            .id(new CustomerId())
            .fullName(new FullName("Anonymous", "Anonymous"))
            .birthDate(null)
            .email(new Email("anonymous@anonymous.com"))
            .phone(new Phone("000-000-0000"))
            .document(new Document("000-00-0000"))
            .promotionNotificationsAllowed(false)
            .archived(true)
            .registeredAt(OffsetDateTime.now())
            .archivedAt(OffsetDateTime.now())
            .loyaltyPoints(new LoyaltyPoints(10))
            .address(Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("Noth Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement("Apt. 114")
                .build());
    }

}
