package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
public class GetNotCompletedOrderQueryHandlerImpl implements GetNotCompletedOrderQueryHandler {

    private final OrderRepository orderRepository;

    public GetNotCompletedOrderQueryHandlerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Transient
    public Result<GetNotCompletedOrdersResponse[], Error> handle() {
        var orders = orderRepository.getAllByStatuses(OrderStatus.ASSIGNED, OrderStatus.CREATED);
        if (orders.isEmpty()) {
            return Result.failure(GeneralErrors.notFound("orders not found", orders));
        }

        var result = orders.stream().map(this::mapToDto).toArray(GetNotCompletedOrdersResponse[]::new);

        return Result.success(result);
    }

    private GetNotCompletedOrdersResponse mapToDto(Order order) {
        return new GetNotCompletedOrdersResponse(order.getId(), order.getDeliveryLocation());
    }
}
