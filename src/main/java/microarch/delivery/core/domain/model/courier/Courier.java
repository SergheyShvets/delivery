package microarch.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Courier extends Aggregate<UUID> {

    private final Volume maxVolume = Volume.mustCreate(20);

    @Getter
    private final String name;

    @Getter
    private Location location;

    @Getter
    private Set<Assignment> assignments = new HashSet<>();

    private Courier(String name, Location location) {
        super(UUID.randomUUID());
        this.name = name;
        this.location = location;
    }

    public static Result<Courier, Error> create(String name, Location location) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");

        return Result.success(new Courier(name, location));
    }

    public UnitResult<Error> addOrder(Order order) {
        var currentValue = assignments.stream().mapToInt(a -> a.getVolume().getValue()).sum();
        var valueWithNewOrder = currentValue + order.getVolume().getValue();

        if (valueWithNewOrder > maxVolume.getValue())
            return UnitResult.failure(GeneralErrors.valueMustBeLessOrEqual("valueWithNewOrder", valueWithNewOrder, maxVolume.getValue()));

        var newAssigned = Assignment.create(order.getId(), order.getVolume(), order.getDeliveryLocation());
        if (newAssigned.isFailure())
            return UnitResult.failure(GeneralErrors.valueIsInvalid("order", order));

        assignments.add(newAssigned.getValue());
        return UnitResult.success();
    }

    public UnitResult<Error> closeAssigned(UUID orderId) {
        var assignmentToClose = assignments.stream().filter(a -> a.getOrderId() == orderId && !a.checkIfCompleted())
                .findFirst()
                .orElse(null);

        if (assignmentToClose == null)
            return UnitResult.failure(GeneralErrors.valueIsInvalid("orderId", orderId));

        return assignmentToClose.completeAssignment(location);
    }

    public UnitResult<Error> moveUp() {
        return setNewLocation(location.getCoordinate_x(), location.getCoordinate_y() + 1);
    }

    public UnitResult<Error> moveDown() {
        return setNewLocation(location.getCoordinate_x(), location.getCoordinate_y() - 1);

    }

    public UnitResult<Error> moveLeft() {
        return setNewLocation(location.getCoordinate_x() - 1, location.getCoordinate_y());

    }

    public UnitResult<Error> moveRight() {
        return setNewLocation(location.getCoordinate_x() + 1, location.getCoordinate_y());
    }

    private UnitResult<Error> setNewLocation(int coordinate_x, int coordinate_y) {
        var result = Location.create(coordinate_x, coordinate_y);
        if (result.isFailure())
            return UnitResult.failure(result.getError());

        location = result.getValue();
        return UnitResult.success();
    }
}
