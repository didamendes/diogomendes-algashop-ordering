package com.diogomendes.algashop.ordering.infrastructure.listener.customer;

import com.diogomendes.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.diogomendes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.diogomendes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerLoyaltyPointService;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.diogomendes.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerNotificationApplicationService customerNotificationService;

    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        NotifyNewRegistrationInput input = new NotifyNewRegistrationInput(
                event.customerId().value(),
                event.fullName().firstName(),
                event.email().value()
        );
        customerNotificationService.notifyNewRegistration(input);
        log.info("CustomerRegisteredEvent listen 1");
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("CustomerArchivedEvent listen 1");
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        customerLoyaltyPointsApplicationService.addLoyaltyPoints(event.customerId().value(), event.orderId().toString());
    }

}
