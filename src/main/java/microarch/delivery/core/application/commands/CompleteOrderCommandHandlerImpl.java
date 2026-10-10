package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CompleteOrderCommandHandlerImpl(CourierRepository courierRepository, OrderRepository orderRepository,
            DomainEventPublisher domainEventPublisher) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        var courierOpt = courierRepository.findById(command.getCourierId());
        if (courierOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("courier", command.getCourierId()));
        }
        var courier = courierOpt.get();

        var orderOpt = orderRepository.findById(command.getOrderId());
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("order with state created", orderOpt));
        }
        var order = orderOpt.get();

        var closeAssignedRes = courier.closeAssignment(order.getId());
        if (closeAssignedRes.isFailure()) {
            return UnitResult.failure(closeAssignedRes.getError());
        }
        var completeOrderRes = order.completeOrder();
        if (completeOrderRes.isFailure()) {
            return UnitResult.failure(completeOrderRes.getError());
        }

        courierRepository.update(courier);
        orderRepository.update(order);
        domainEventPublisher.publish(List.of(courier, order));
        return UnitResult.success();
    }
}
