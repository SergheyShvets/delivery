package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.mapper.DeliveryMapper;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.core.application.queries.GetAllCourierQueryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {
    private final GetAllCourierQueryHandler getAllCourierQueryHandler;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var handlQeueryResult = getAllCourierQueryHandler.handle();
        if (handlQeueryResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var models = Arrays.stream(handlQeueryResult.getValue()).map(DeliveryMapper.INSTANCE::toHttp).toList();
        return ResponseEntity.ok(models);
    }
}
