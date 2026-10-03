package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.services.OrderAllocationService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {
    private final OrderAllocationService orderAllocationService;
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    public AssignOrderCommandHandlerImpl(
            OrderAllocationService orderAllocationService,
            CourierRepository courierRepository,
            OrderRepository orderRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.orderAllocationService = orderAllocationService;
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public UnitResult<Error> handle() {
        var orderOpt = orderRepository.getOneWithStateCreated();
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("order with state created", orderOpt));
        }
        var order = orderOpt.get();

        var couriers = courierRepository.getAll();
        if (couriers.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("couriers not found", couriers));
        }

        var allocatedCourierWithOrderRes = orderAllocationService.allocateOrder(order, couriers.toArray(Courier[]::new));
        if (allocatedCourierWithOrderRes.isFailure()) {
            return UnitResult.failure(allocatedCourierWithOrderRes.getError());
        }
        var courierWithOrder = allocatedCourierWithOrderRes.getValue();

        courierRepository.update(courierWithOrder);
        orderRepository.update(order);
        domainEventPublisher.publish(List.of(courierWithOrder, order));

        return UnitResult.success();
    }
}
