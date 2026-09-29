package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import libs.errs.Result;
import libs.errs.Error;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {
    private final CourierRepository courierRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CreateCourierCommandHandlerImpl(CourierRepository courierRepository,
            DomainEventPublisher domainEventPublisher) {
        this.courierRepository = courierRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public Result<UUID, Error> handle(CreateCourierCommand command) {
        var courierResult = Courier.create(command.getName(), command.getLocation());
        if (courierResult.isFailure()) {
            return Result.failure(courierResult.getError());
        }
        var courier = courierResult.getValue();

        courierRepository.save(courier);
        domainEventPublisher.publish(List.of(courier));

        return Result.success(courierResult.getValue().getId());
    }

}
