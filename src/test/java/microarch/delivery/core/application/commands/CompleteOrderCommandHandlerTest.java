package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CompleteOrderCommandHandlerTest {
    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

    @Test
    void CreateCourierCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        Location location = Location.create(5, 5).getValue();

        String name = "Alex";
        var courier = Courier.create(name, location).getValue();

        Volume volume = Volume.mustCreate(5);
        Order order = Order.create(UUID.randomUUID(), location, volume).getValue();

        // set order on courier;
        courier.addOrder(order.getId(), order.getVolume(), order.getDeliveryLocation());
        order.assignOrder();

        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.getOneWithStateCreated()).thenReturn(Optional.of(order));

        var handler = new CompleteOrderCommandHandlerImpl(courierRepository, orderRepository, domainEventPublisher);
        var command = CompleteOrderCommand.create(courier.getId(), order.getId()).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).update(courier);
        verify(orderRepository).update(order);
        // Check if completed order
        assertThat(courierRepository.findById(courier.getId()).get().getAssignments()[0].checkIfCompleted()).isTrue();
        assertThat(orderRepository.getOneWithStateCreated().get().getStatus()).isEqualTo(OrderStatus.COMPLETED);

    }
}
