package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class CreateOrderCommandHandlerTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

    @Test
    void CreateOrderCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        UUID basketId = UUID.randomUUID();
        String country = "Russia";
        String city = "Moscow";
        String street = "lenina";
        String house = "18";
        String apartment = "121";
        int volume = 5;

        var handler = new CreateOrderCommandHandlerImpl(
                orderRepository,
                domainEventPublisher
        );
        var command = CreateOrderCommand.create(
                basketId,
                country,
                city,
                street,
                house,
                apartment,
                volume
        ).getValue();

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
    }
}
