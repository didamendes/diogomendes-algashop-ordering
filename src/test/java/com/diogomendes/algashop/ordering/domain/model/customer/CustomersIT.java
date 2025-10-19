package com.diogomendes.algashop.ordering.domain.model.customer;

import com.diogomendes.algashop.ordering.domain.model.commons.Email;
import com.diogomendes.algashop.ordering.domain.model.commons.FullName;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.brandNewCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
class CustomersIT {

    private Customers customers;

    @Autowired
    public CustomersIT(Customers customers) {
        this.customers = customers;
    }

    @Test
    public void shouldPersistAndFirst() {
        Customer originalCustomer = brandNewCustomer().build();
        CustomerId customerId = originalCustomer.id();
        customers.add(originalCustomer);

        Optional<Customer> possibleCustomer = customers.ofId(customerId);

        assertThat(possibleCustomer).isPresent();

        Customer savedCustomer = possibleCustomer.get();

        assertThat(savedCustomer).satisfies(
                s -> assertThat(s.id()).isEqualTo(customerId)
        );
    }

    @Test
    public void shouldUpdateExistingCustomer() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        customer = customers.ofId(customer.id()).orElseThrow();
        customer.archive();

        customers.add(customer);

        Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

        assertThat(savedCustomer.archivedAt()).isNotNull();
        assertThat(savedCustomer.archived()).isTrue();
    }

    @Test
    public void shouldIdNotAllowStaleUpdates() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        Customer customerT1 = customers.ofId(customer.id()).orElseThrow();
        Customer customerT2 = customers.ofId(customer.id()).orElseThrow();

        customerT1.archive();
        customers.add(customerT1);

        customerT2.changeName(new FullName("Diogo", "Mendes"));
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> customers.add(customerT2));

        Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

        assertThat(savedCustomer.archivedAt()).isNotNull();
        assertThat(savedCustomer.archived()).isTrue();
    }

    @Test
    public void shouldCountExistingOrders() {
        assertThat(customers.count()).isZero();

        Customer customer1 = brandNewCustomer().build();
        customers.add(customer1);

        Customer customer2 = brandNewCustomer().build();
        customers.add(customer2);

        assertThat(customers.count()).isEqualTo(2);
    }

    @Test
    public void shouldReturnValidateIfOrderExists() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        assertThat(customers.exists(customer.id())).isTrue();
        assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    public void shouldFindByEmail() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        Optional<Customer> customerOptional = customers.ofEmail(customer.email());

        assertThat(customerOptional).isPresent();
    }

    @Test
    public void shouldNotFindByEmailIfNoCustomerExistsWithEmail() {
        Optional<Customer> customerOptional = customers.ofEmail(new Email(UUID.randomUUID() + "@email.com"));

        assertThat(customerOptional).isNotPresent();
    }

    @Test
    public void shouldReturnIfEmailIsInUse() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
        assertThat(customers.isEmailUnique(customer.email(), new CustomerId())).isFalse();
        assertThat(customers.isEmailUnique(new Email(UUID.randomUUID() + "@email.com"), new CustomerId())).isTrue();
    }

}