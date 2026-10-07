package microarch.delivery.core.domain.model.courier;

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
import java.util.*;
import static libs.errs.Guard.againstGreaterThan;

@Entity
@Table(name = "couriers")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Courier extends Aggregate<UUID> {
    private static final int MAX_STEPS_TO_MOVE = 1;

    @Transient
    private final Volume maxVolume = Volume.mustCreate(20);

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "courier_id", nullable = false)
    private final Set<Assignment> assignments = new HashSet<>();

    @Getter
    @Column(name = "name")
    private final String name;

    @Getter
    @Embedded
    private Location location;

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

    public Assignment[] getAssignments() {
        return assignments.toArray(Assignment[]::new);
    }

    public UnitResult<Error> addOrder(UUID orderId, Volume newVolume, Location deliveryLocation) {
        Volume[] currentVolumes = assignments.stream().map(Assignment::getVolume).toArray(Volume[]::new);
        var volumeWithNewOrderRes = newVolume.addAndCreate(currentVolumes);
        if (volumeWithNewOrderRes.isFailure())
            return UnitResult.failure(volumeWithNewOrderRes.getError());

        var volumeWithNewOrder = volumeWithNewOrderRes.getValue();
        if (maxVolume.compareTo(volumeWithNewOrder) < 0)
            return UnitResult.failure(GeneralErrors.valueMustBeLessOrEqual("volumeWithNewOrder",
                    volumeWithNewOrder.getValue(), maxVolume.getValue()));

        var newAssigned = Assignment.create(orderId, newVolume, deliveryLocation);
        if (newAssigned.isFailure())
            return UnitResult.failure(GeneralErrors.valueIsInvalid("newAssigned", newAssigned));

        assignments.add(newAssigned.getValue());
        return UnitResult.success();
    }

    public UnitResult<Error> closeAssignment(UUID orderId) {
        var assignmentToClose = assignments.stream()
                .filter(a -> orderId.equals(a.getOrderId()) && !a.checkIfCompleted()).findFirst().orElse(null);

        if (assignmentToClose == null)
            return UnitResult.failure(GeneralErrors.valueIsInvalid("orderId", orderId));

        return assignmentToClose.complete(location);
    }

    public UnitResult<Error> moveToNewLocation(Location newLocation) {
        if (newLocation == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("newLocation"));
        }

        var steps = location.countStepsTo(newLocation);
        var cannotMoveErr = againstGreaterThan(steps, MAX_STEPS_TO_MOVE, "location");
        if (cannotMoveErr != null)
            return UnitResult.failure(cannotMoveErr);

        location = newLocation;
        return UnitResult.success();
    }
}
