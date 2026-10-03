package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GetNotCompletedOrderQueryHandlerTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Test
    void GetAllCourierCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        var createdOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5)).getValue();

        var assignedOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5)).getValue();
        assignedOrder.assignOrder();

        var completedOrder = Order.create(UUID.randomUUID(), Location.generateRandomLocation(), Volume.mustCreate(5)).getValue();
        completedOrder.assignOrder();
        completedOrder.completeOrder();

        var orders = new Order[]{
                createdOrder,
                assignedOrder,
                completedOrder
        };

        var filteredOrders = Arrays.stream(orders).filter(a -> a.getStatus() != OrderStatus.COMPLETED).toList();

        when(orderRepository.getAllByStatuses(OrderStatus.ASSIGNED, OrderStatus.CREATED))
                .thenReturn(filteredOrders);

        var handler = new GetNotCompletedOrderQueryHandlerImpl(orderRepository);
        var responseResult = handler.handle();

        assertThat(responseResult.isSuccess()).isTrue();
        var response = responseResult.getValue();
        assertThat(response.length).isEqualTo(filteredOrders.size());
        for (int i = 0; i < response.length; i++) {
            assertThat(response[i].orderId()).isEqualTo(filteredOrders.get(i).getId());
            assertThat(response[i].deliveryLocation()).isEqualTo(filteredOrders.get(i).getDeliveryLocation());
        }
    }
}
