package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.Customer;
import com.diogomendes.algashop.ordering.domain.model.repository.Customers;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static java.time.LocalDate.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    @Mock
    private Customers customers;

    @InjectMocks
    private CustomerRegistrationService customerRegistrationService;

    @Test
    public void shouldRegister() {
        when(customers.isEmailUnique(any(Email.class), any(CustomerId.class)))
                .thenReturn(true);

        Customer customer = customerRegistrationService.register(
                new FullName("John", "Doe"),
                new BirthDate(of(1991, 7, 5)),
                new Email("johndoe@email.com"),
                new Phone("478-256-2604"),
                new Document("255-08-0578"),
                true,
                Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("Yostfort")
                        .state("South Carolina")
                        .zipCode(new ZipCode("70283"))
                        .complement("Apt 901")
                        .build()
        );

        Assertions.assertThat(customer.fullName()).isEqualTo(new FullName("John", "Doe"));
    }

}