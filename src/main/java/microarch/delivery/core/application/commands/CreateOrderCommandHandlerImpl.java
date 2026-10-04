package microarch.delivery.core.application.commands;

import jakarta.transaction.Transactional;
import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CreateOrderCommandHandlerImpl(OrderRepository orderRepository, DomainEventPublisher domainEventPublisher) {
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public Result<UUID, Error> handle(CreateOrderCommand command) {
        var orderOpt = orderRepository.findById(command.getBasketId());
        if (orderOpt.isEmpty()) {
            var orderResult = Order.create(command.getBasketId(), command.getDeliveryLocale(), command.getVolume());
            if (orderResult.isFailure()) {
                return Result.failure(orderResult.getError());
            }

            var order = orderResult.getValue();
            orderRepository.save(order);
            domainEventPublisher.publish(List.of(order));
            return Result.success(orderResult.getValue().getId());
        }

        return Result.success(orderOpt.get().getId());
    }
}
