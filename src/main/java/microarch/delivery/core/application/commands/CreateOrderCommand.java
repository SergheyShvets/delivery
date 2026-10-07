package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Address;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {
    private final UUID basketId;
    private final Address address;
    private final Location deliveryLocation;
    private final Volume volume;

    public static Result<CreateOrderCommand, Error> create(
            UUID basketId,
            String country,
            String city,
            String street,
            String house,
            String apartment,
            int volume
    ) {
        var err = Guard.againstNullOrEmpty(basketId, "basketId");
        if (err != null) {
            return Result.failure(err);
        }

        var addressResult = Address.create(
                country,
                city,
                street,
                house,
                apartment
        );
        if (addressResult.isFailure()) {
            return Result.failure(addressResult.getError());
        }
        var address = addressResult.getValue();

        var deliveryLocation = Location.generateRandomLocation();

        var volumeResult = Volume.create(volume);
        if (volumeResult.isFailure()) {
            return Result.failure(volumeResult.getError());
        }

        return Result.success(new CreateOrderCommand(basketId, address, deliveryLocation, volumeResult.getValue()));
    }
}
