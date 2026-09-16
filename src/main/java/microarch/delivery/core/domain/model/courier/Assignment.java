package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

import static libs.errs.Guard.againstGreaterThan;

public class Assignment extends BaseEntity<UUID> {
    private static final int MAX_STEPS_TO_COMPLETE = 1;

    @Getter
    private final UUID orderId;

    @Getter
    private final Volume volume;

    @Getter
    private final Location location;

    private Status status;

    private Assignment(UUID orderId, Volume volume, Location location) {
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = Status.Assigned;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        return Result.success(new Assignment(orderId, volume, location));
    }

    public UnitResult<Error> completeAssignment(Location location) {
        var steps = this.location.countStepsTo(location);
        var cannotCompleteErr = againstGreaterThan(steps, MAX_STEPS_TO_COMPLETE, "location");
        if (cannotCompleteErr != null) return UnitResult.failure(cannotCompleteErr);
        this.status = Status.Completed;
        return UnitResult.success();
    }

    public Boolean checkIfCompleted() {
        return status == Status.Completed;
    }

    private enum Status {
        Assigned,
        Completed;
    }
}
