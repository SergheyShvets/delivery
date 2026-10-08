package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CompleteOrderApi;
import microarch.delivery.core.application.commands.CompleteOrderCommand;
import microarch.delivery.core.application.commands.CompleteOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompleteOrderController implements CompleteOrderApi {
    private final CompleteOrderCommandHandler completeOrderCommandHandler;

    @Override
    public ResponseEntity<Void> completeOrder(UUID courierId, UUID orderId) {
        var completeOrderCommandResult = CompleteOrderCommand.create(courierId, orderId);
        if (completeOrderCommandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }
        var command = completeOrderCommandResult.getValue();

        var handleCommandResult = completeOrderCommandHandler.handle(command);
        if (handleCommandResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }
}
