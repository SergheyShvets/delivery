package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetNotCompletedOrderQueryHandlerTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Test
    void GetAllCourierCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        var createdOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5))
                .getValue();

        var assignedOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5))
                .getValue();
        assignedOrder.assignOrder();

        var completedOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5))
                .getValue();
        completedOrder.assignOrder();
        completedOrder.completeOrder();

        var orders = new Order[] { createdOrder, assignedOrder, completedOrder };

        var filteredOrders = Arrays.stream(orders).filter(a -> a.getStatus() != OrderStatus.COMPLETED).toList();

        var actualStatuses = EnumSet.of(OrderStatus.ASSIGNED, OrderStatus.CREATED);

        when(orderRepository.findAllByStatusIn(actualStatuses)).thenReturn(filteredOrders);

        var handler = new GetNotCompletedOrdersQueryHandlerImpl(orderRepository);
        var responseResult = handler.handle();

        assertThat(responseResult.isSuccess()).isTrue();
        var response = responseResult.getValue();
        assertThat(response.length).isEqualTo(filteredOrders.size());
        for (int i = 0; i < response.length; i++) {
            assertThat(response[i].orderId()).isEqualTo(filteredOrders.get(i).getId());
            assertThat(response[i].coordinate_x())
                    .isEqualTo(filteredOrders.get(i).getDeliveryLocation().getCoordinate_x());
            assertThat(response[i].coordinate_y())
                    .isEqualTo(filteredOrders.get(i).getDeliveryLocation().getCoordinate_y());
        }
    }
}
