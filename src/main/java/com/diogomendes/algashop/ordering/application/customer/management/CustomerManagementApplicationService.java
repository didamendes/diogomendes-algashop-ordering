package com.diogomendes.algashop.ordering.application.customer.management;

import com.diogomendes.algashop.ordering.application.commons.AddressData;
import com.diogomendes.algashop.ordering.application.utility.Mapper;
import com.diogomendes.algashop.ordering.domain.model.commons.*;
import com.diogomendes.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final Customers customers;
    private final Mapper mapper;
    private final CustomerRegistrationService customerRegistrationService;

    @Transactional
    public UUID create(CustomerInput input) {
        requireNonNull(input);
        AddressData address = input.getAddress();

        Customer customer = customerRegistrationService.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.getPromotionNotificaionsAllowed(),
                Address.builder()
                        .zipCode(new ZipCode(address.getZipCode()))
                        .state(address.getState())
                        .city(address.getCity())
                        .neighborhood(address.getNeighborhood())
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .build()
        );

        customers.add(customer);

        return customer.id().value();
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId) {
        requireNonNull(customerId);

        Customer customer = customers.ofId(new CustomerId(customerId))
                .orElseThrow(CustomerNotFoundException::new);

        return mapper.convert(customer, CustomerOutput.class);
    }

    @Transactional
    public void update(UUID rawCustomerId, CustomerUpdateInput input) {
        requireNonNull(input);
        requireNonNull(rawCustomerId);

        Customer customer = customers.ofId(new CustomerId(rawCustomerId))
                .orElseThrow(CustomerNotFoundException::new);

        customer.changeName(new FullName(input.getFirstName(), input.getLastName()));
        customer.changePhone(new Phone(input.getPhone()));

        if (TRUE.equals(input.getPromotionNotificaionsAllowed())) {
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }

        AddressData address = input.getAddress();

        customer.changeAddress(Address.builder()
                .zipCode(new ZipCode(address.getZipCode()))
                .state(address.getState())
                .city(address.getCity())
                .neighborhood(address.getNeighborhood())
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .build());

        customers.add(customer);
    }

    @Transactional
    public void archive(UUID rawCustomerId) {
        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId)
                .orElseThrow(CustomerNotFoundException::new);
        customer.archive();
        customers.add(customer);
    }

    @Transactional
    public void changeEmail(UUID rawCustomerId, String newEmail) {
        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId)
                .orElseThrow(CustomerNotFoundException::new);
        customerRegistrationService.changeEmail(customer, new Email(newEmail));
        customers.add(customer);
    }

}
