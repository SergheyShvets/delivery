package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.OrderAllocationService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AssignOrdersCommandHandlerTest {
    private final OrderAllocationService orderAllocationService = mock(OrderAllocationService.class);
    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

    @Test
    void AssignOrderCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        Location location = Location.create(5, 5).getValue();

        Volume volume = Volume.mustCreate(5);
        Order order = Order.create(UUID.randomUUID(), location, volume).getValue();

        var couriers = new Courier[] { Courier.create("name", Location.create(1, 1).getValue()).getValue(),
                Courier.create("name1", Location.create(10, 10).getValue()).getValue(),
                Courier.create("name2", Location.create(4, 4).getValue()).getValue() };

        when(courierRepository.getAll()).thenReturn(Arrays.stream(couriers).toList());
        when(orderRepository.getOneWithStateCreated()).thenReturn(Optional.of(order));
        when(orderAllocationService.allocateOrder(order, couriers)).thenReturn(Result.success(couriers[2]));

        var handler = new AssignOrdersCommandHandlerImpl(orderAllocationService, courierRepository, orderRepository,
                domainEventPublisher);
        var result = handler.handle();

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).update(couriers[2]);
        verify(orderRepository).update(order);
    }
}
