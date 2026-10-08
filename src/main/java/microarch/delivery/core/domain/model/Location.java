package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Location extends ValueObject<Location> {
    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 10;

    @Column(name = "coordinate_x")
    private final int coordinate_x;

    @Column(name = "coordinate_y")
    private final int coordinate_y;

    public static Result<Location, Error> create(int coordinate_x, int coordinate_y) {
        var lessMinErrX = Guard.againstOutOfRange(coordinate_x, MIN_COORDINATE, MAX_COORDINATE, "coordinate_x");
        var lessMinErrY = Guard.againstOutOfRange(coordinate_y, MIN_COORDINATE, MAX_COORDINATE, "coordinate_y");
        if (lessMinErrX != null)
            return Result.failure(lessMinErrX);
        if (lessMinErrY != null)
            return Result.failure(lessMinErrY);

        return Result.success(new Location(coordinate_x, coordinate_y));
    }

    public static Location generateRandomLocation() {
        Random rn = new Random();
        int coordinate_x = rn.nextInt(MAX_COORDINATE) + MIN_COORDINATE;
        int coordinate_y = rn.nextInt(MAX_COORDINATE) + MIN_COORDINATE;
        return create(coordinate_x, coordinate_y).getValue();
    }

    public static Location mustCreate(int coordinate_x, int coordinate_y) {
        return create(coordinate_x, coordinate_y).getValueOrThrow();
    }

    public int countStepsTo(Location otherLocation) {
        int horizontalSteps = Math.abs(otherLocation.coordinate_x - this.coordinate_x);
        int verticalSteps = Math.abs(otherLocation.coordinate_y - this.coordinate_y);
        return horizontalSteps + verticalSteps;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(coordinate_x, coordinate_y);
    }
}
