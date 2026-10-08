package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.mapper.DeliveryMapper;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {
    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var handlQeueryResult = getNotCompletedOrdersQueryHandler.handle();
        if (handlQeueryResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var models = Arrays.stream(handlQeueryResult.getValue()).map(DeliveryMapper.INSTANCE::toHttp).toList();
        return ResponseEntity.ok(models);
    }
}
