package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
public class GetAllCourierQueryHandlerImpl implements GetAllCourierQueryHandler {

    private final CourierRepository courierRepository;

    public GetAllCourierQueryHandlerImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Transient
    public Result<GetAllCourierResponse[], Error> handle() {
        var couriers = courierRepository.getAll();

        var result = couriers.stream().map(this::mapToDto).toArray(GetAllCourierResponse[]::new);

        return Result.success(result);
    }

    private GetAllCourierResponse mapToDto(Courier courier) {
        return new GetAllCourierResponse(courier.getId(), courier.getName(), courier.getLocation().getCoordinate_x(),
                courier.getLocation().getCoordinate_y());
    }
}
