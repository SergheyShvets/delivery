package microarch.delivery.core.application.commands;

import jakarta.transaction.Transactional;
import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final GeoClient geoClient;
    private final DomainEventPublisher domainEventPublisher;

    public CreateOrderCommandHandlerImpl(
            OrderRepository orderRepository,
            GeoClient geoClient,
            DomainEventPublisher domainEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.geoClient = geoClient;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public Result<UUID, Error> handle(CreateOrderCommand command) {
        var orderOpt = orderRepository.findById(command.getBasketId());
        if (orderOpt.isPresent()) {
            return Result.failure(GeneralErrors.valueIsInvalid("order is exist", command.getBasketId()));
        }

        var deliveryLocation = geoClient.getLocation(command.getAddress());

        var orderResult = Order.create(command.getBasketId(), deliveryLocation, command.getVolume());
        if (orderResult.isFailure()) {
            return Result.failure(orderResult.getError());
        }
        var order = orderResult.getValue();

        orderRepository.save(order);
        domainEventPublisher.publish(List.of(order));
        return Result.success(orderResult.getValue().getId());
    }
}
