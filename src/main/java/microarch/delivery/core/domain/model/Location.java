package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.Error;

import java.util.List;

public final class Location extends ValueObject<Location> {
    private static final int[] minMaxRange = {1, 10};

    private final int coordinate_x;
    private final int coordinate_y;

    private Location(int coordinateX, int coordinateY) {
        coordinate_x = coordinateX;
        coordinate_y = coordinateY;
    }

    public static Result<Location, Error> create(int coordinate_x, int coordinate_y) {
        var lessMinErrX = Guard.againstOutOfRange(coordinate_x, minMaxRange[0], minMaxRange[1], "coordinate_x");
        var lessMinErrY = Guard.againstOutOfRange(coordinate_y, minMaxRange[0], minMaxRange[1], "coordinate_y");
        if (lessMinErrX != null) return Result.failure(lessMinErrX);
        if (lessMinErrY != null) return Result.failure(lessMinErrY);

        return Result.success(new Location(coordinate_x, coordinate_y));
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
