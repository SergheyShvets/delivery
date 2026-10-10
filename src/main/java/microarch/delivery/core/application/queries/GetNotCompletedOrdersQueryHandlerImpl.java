package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.EnumSet;

@Service
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private final OrderRepository orderRepository;

    public GetNotCompletedOrdersQueryHandlerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transient
    public Result<GetNotCompletedOrdersResponse[], Error> handle() {
        var actualStatuses = EnumSet.of(OrderStatus.ASSIGNED, OrderStatus.CREATED);
        var orders = orderRepository.findAllByStatusIn(actualStatuses);

        var result = orders.stream().map(this::mapToDto).toArray(GetNotCompletedOrdersResponse[]::new);

        return Result.success(result);
    }

    private GetNotCompletedOrdersResponse mapToDto(Order order) {
        return new GetNotCompletedOrdersResponse(order.getId(), order.getDeliveryLocation().getCoordinate_x(),
                order.getDeliveryLocation().getCoordinate_y());
    }
}
