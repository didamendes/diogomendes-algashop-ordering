package com.diogomendes.algashop.ordering.infrastructure.listener.customer;

import com.diogomendes.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.diogomendes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.diogomendes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.diogomendes.algashop.ordering.domain.model.commons.Email;
import com.diogomendes.algashop.ordering.domain.model.commons.FullName;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.diogomendes.algashop.ordering.domain.model.order.OrderId;
import com.diogomendes.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CustomerEventListenerIT {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @MockitoBean
    private CustomerNotificationApplicationService notificationApplicationService;

    @Test
    public void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(
                new OrderReadyEvent(
                        new OrderId(),
                        new CustomerId(),
                        OffsetDateTime.now()
                )
        );

        verify(customerEventListener).listen(any(OrderReadyEvent.class));
        verify(loyaltyPointsApplicationService).addLoyaltyPoints(
                any(UUID.class), any(String.class));
    }

    @Test
    public void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(
                new CustomerRegisteredEvent(
                        new CustomerId(),
                        OffsetDateTime.now(),
                        new FullName("John", "Doe"),
                        new Email("john.doe@email.com")
                )
        );

        verify(customerEventListener).listen(any(CustomerRegisteredEvent.class));
        verify(notificationApplicationService).notifyNewRegistration(
                any(NotifyNewRegistrationInput.class));
    }

}