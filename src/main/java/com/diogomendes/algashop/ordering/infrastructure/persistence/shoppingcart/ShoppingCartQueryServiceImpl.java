package com.diogomendes.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.diogomendes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.diogomendes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.diogomendes.algashop.ordering.application.utility.Mapper;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

    private final Mapper mapper;
    private final ShoppingCartPersistenceEntityRepository persistenceRepository;

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        return persistenceRepository.findById(shoppingCartId)
                .map(s -> mapper.convert(s, ShoppingCartOutput.class))
                .orElseThrow(ShoppingCartNotFoundException::new);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        return persistenceRepository.findByCustomer_Id(customerId)
                .map(s -> mapper.convert(s, ShoppingCartOutput.class))
                .orElseThrow(ShoppingCartNotFoundException::new);
    }
}
