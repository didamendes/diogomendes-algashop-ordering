package com.diogomendes.algashop.ordering.infrastructure.listener.shoppingcart;

import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartEventListener {

    @EventListener
    public void listen(ShoppingCartCreatedEvent event) {
    }

    @EventListener
    public void listen(ShoppingCartEmptiedEvent event) {

    }

    @EventListener
    public void listen(ShoppingCartItemAddedEvent event) {

    }

    @EventListener
    public void listen(ShoppingCartItemRemovedEvent event) {

    }

}
