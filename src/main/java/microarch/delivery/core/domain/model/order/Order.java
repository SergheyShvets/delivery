package microarch.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;


public class Order extends Aggregate<UUID> {

    @Getter
    private final Location deliveryLocation;
    @Getter
    private final Volume volume;

    @Getter
    private OrderStatus status;

    private Order(UUID basketId, Location deliveryLocation, Volume volume) {
        super(basketId);
        this.deliveryLocation = deliveryLocation;
        this.volume = volume;
        this.status = OrderStatus.Created;
    }

    public static Result<Order, Error> create(UUID basketId, Location deliveryLocation, Volume volume) {
        Objects.requireNonNull(basketId, "basketId");
        Objects.requireNonNull(deliveryLocation, "deliveryLocation");
        Objects.requireNonNull(volume, "volume");

        return Result.success(new Order(basketId, deliveryLocation, volume));
    }

    public UnitResult<Error> assignOrder() {
        if (status != OrderStatus.Created)
            return UnitResult.failure(GeneralErrors.valueIsRequired("OrderStatus.Created"));
        this.status = OrderStatus.Assigned;
        return UnitResult.success();
    }

    public UnitResult<Error> completeOrder() {
        if (status != OrderStatus.Assigned)
            return UnitResult.failure(GeneralErrors.valueIsRequired("OrderStatus.Assigned"));

        this.status = OrderStatus.Completed;
        return UnitResult.success();
    }
}
