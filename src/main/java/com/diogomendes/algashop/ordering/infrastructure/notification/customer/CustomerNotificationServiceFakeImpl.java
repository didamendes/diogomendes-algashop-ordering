package com.diogomendes.algashop.ordering.infrastructure.notification.customer;

import com.diogomendes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationApplicationService {

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput input) {
        log.info("[FAKE IMPLEMENTATION] Notifying new customer registration: {} - {}", input.firstName(),
                input.email());
    }
}
