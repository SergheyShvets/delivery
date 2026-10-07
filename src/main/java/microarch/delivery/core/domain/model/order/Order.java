package microarch.delivery.core.domain.model.order;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    @Getter
    private final Location deliveryLocation;

    @Embedded
    @Getter
    private final Volume volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Getter
    private OrderStatus status;

    private Order(UUID basketId, Location deliveryLocation, Volume volume) {
        super(basketId);
        this.deliveryLocation = deliveryLocation;
        this.volume = volume;
        this.status = OrderStatus.CREATED;
    }

    public static Result<Order, Error> create(UUID basketId, Location deliveryLocation, Volume volume) {
        Objects.requireNonNull(basketId, "basketId");
        Objects.requireNonNull(deliveryLocation, "deliveryLocation");
        Objects.requireNonNull(volume, "volume");

        return Result.success(new Order(basketId, deliveryLocation, volume));
    }

    public UnitResult<Error> assignOrder() {
        if (status != OrderStatus.CREATED)
            return UnitResult.failure(GeneralErrors.valueIsRequired("OrderStatus.Created"));
        this.status = OrderStatus.ASSIGNED;
        return UnitResult.success();
    }

    public UnitResult<Error> completeOrder() {
        if (status != OrderStatus.ASSIGNED)
            return UnitResult.failure(GeneralErrors.valueIsRequired("OrderStatus.Assigned"));
        this.status = OrderStatus.COMPLETED;
        return UnitResult.success();
    }
}
