package microarch.delivery.core.domain.model.courier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

import static libs.errs.Guard.againstGreaterThan;

@Entity
@Table(name = "assignments")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class Assignment extends BaseEntity<UUID> {
    private static final int MAX_STEPS_TO_COMPLETE = 1;

    @Column(name = "order_id")
    private final UUID orderId;

    @Embedded
    private final Volume volume;

    @Embedded
    private final Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    private Assignment(UUID orderId, Volume volume, Location location) {
        super(UUID.randomUUID());
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = Status.ASSIGNED;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        return Result.success(new Assignment(orderId, volume, location));
    }

    public UnitResult<Error> complete(Location location) {
        var steps = this.location.countStepsTo(location);
        var cannotCompleteErr = againstGreaterThan(steps, MAX_STEPS_TO_COMPLETE, "location");
        if (cannotCompleteErr != null)
            return UnitResult.failure(cannotCompleteErr);
        this.status = Status.COMPLETED;
        return UnitResult.success();
    }

    public Boolean checkIfCompleted() {
        return status == Status.COMPLETED;
    }

    private enum Status {
        ASSIGNED, COMPLETED;

        @JsonCreator
        public static Status fromValue(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }
    }
}
