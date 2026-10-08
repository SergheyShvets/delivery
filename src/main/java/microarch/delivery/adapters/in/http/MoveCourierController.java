package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.MoveCourierApi;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.core.application.commands.MoveCourierCommand;
import microarch.delivery.core.application.commands.MoveCourierCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MoveCourierController implements MoveCourierApi {
    private final MoveCourierCommandHandler MoveCourierCommandHandler;

    @Override
    public ResponseEntity<Void> moveCourier(UUID courierId, Location location) {
        var MoveCourierCommandResult = MoveCourierCommand.create(courierId, location.getX(), location.getY());
        if (MoveCourierCommandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }
        var command = MoveCourierCommandResult.getValue();

        var handleCommandResult = MoveCourierCommandHandler.handle(command);
        if (handleCommandResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }
}
