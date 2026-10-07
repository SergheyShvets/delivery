package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MoveCourierCommandHandlerImpl implements MoveCourierCommandHandler {
    private final CourierRepository courierRepository;
    private final DomainEventPublisher domainEventPublisher;

    public MoveCourierCommandHandlerImpl(CourierRepository courierRepository,
                                         DomainEventPublisher domainEventPublisher) {
        this.courierRepository = courierRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Transactional
    public UnitResult<Error> handle(MoveCourierCommand command) {
        var courierOpt = courierRepository.findById(command.getCourierId());
        if (courierOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("courier", command.getCourierId()));
        }
        var courier = courierOpt.get();
        var newLocation = command.getNewLocation();
        var movedCourierResult = courier.moveToNewLocation(newLocation);
        if (movedCourierResult.isFailure()) {
            return UnitResult.failure(movedCourierResult.getError());
        }

        courierRepository.update(courier);
        domainEventPublisher.publish(List.of(courier));

        return UnitResult.success();
    }

}
