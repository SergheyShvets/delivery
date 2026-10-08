package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {
    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        var CreateOrderCommandResult = CreateOrderCommand.create(newOrder.getId(), newOrder.getAddress().getCountry(),
                newOrder.getAddress().getCity(), newOrder.getAddress().getStreet(), newOrder.getAddress().getHouse(),
                newOrder.getAddress().getApartment(), newOrder.getVolume());
        if (CreateOrderCommandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }
        var command = CreateOrderCommandResult.getValue();

        var handleCommandResult = createOrderCommandHandler.handle(command);
        if (handleCommandResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }
}
