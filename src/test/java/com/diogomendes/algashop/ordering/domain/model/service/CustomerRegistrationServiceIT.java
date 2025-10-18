package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.Customer;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomerRegistrationServiceIT {

    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @Test
    public void shouldRegister() {
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
                        .complement("Apt. 901")
                        .build()
        );

        assertThat(customer.fullName()).isEqualTo(new FullName("John", "Doe"));
        assertThat(customer.email()).isEqualTo(new Email("johndoe@email.com"));
    }

}
