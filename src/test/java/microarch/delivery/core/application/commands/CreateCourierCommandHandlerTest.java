package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CreateCourierCommandHandlerTest {
    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

    @Test
    void CreateCourierCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        String name = "Alex";

        var handler = new CreateCourierCommandHandlerImpl(courierRepository, domainEventPublisher);
        var command = CreateCourierCommand.create(name).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
    }
}
