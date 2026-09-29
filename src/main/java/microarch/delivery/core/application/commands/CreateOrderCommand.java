package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {
    private final UUID basketId;
    private final Location deliveryLocale;
    private final Volume volume;

    public static Result<CreateOrderCommand, Error> create(UUID basketId, int coordinate_x, int coordinate_y,
            int volumeValue) {
        var err = Guard.againstNullOrEmpty(basketId, "basketId");
        if (err != null) {
            return Result.failure(err);
        }

        var deliveryLocaleResult = Location.create(coordinate_x, coordinate_y);
        if (deliveryLocaleResult.isFailure()) {
            return Result.failure(deliveryLocaleResult.getError());
        }

        var volumeResult = Volume.create(volumeValue);
        if (volumeResult.isFailure()) {
            return Result.failure(volumeResult.getError());
        }

        return Result
                .success(new CreateOrderCommand(basketId, deliveryLocaleResult.getValue(), volumeResult.getValue()));
    }
}
