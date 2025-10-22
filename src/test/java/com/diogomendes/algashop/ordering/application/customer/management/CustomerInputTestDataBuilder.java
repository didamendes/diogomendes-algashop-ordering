package com.diogomendes.algashop.ordering.application.customer.management;

import com.diogomendes.algashop.ordering.application.commons.AddressData;
import com.diogomendes.algashop.ordering.application.customer.management.CustomerInput.CustomerInputBuilder;

import static java.time.LocalDate.of;

public class CustomerInputTestDataBuilder {

    public static CustomerInputBuilder aCustomer() {
        return CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(of(1991, 7,5))
                .document("255-08-0578")
                .phone("478-256-2604")
                .email("johndoe@email.com")
                .promotionNotificaionsAllowed(false)
                .address(AddressData.builder()
                        .street("Bourbon Street")
                        .number("1200")
                        .complement("Apt. 901")
                        .neighborhood("North Ville")
                        .city("Yostfort")
                        .state("South Carolina")
                        .zipCode("70283")
                        .build());
    }

}
