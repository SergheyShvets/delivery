package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCourierCommand {
    private final String name;
    private final Location location;

    public static Result<CreateCourierCommand, Error> create(String name, int coordinate_x, int coordinate_y) {
        var errName = Guard.againstNullOrEmpty(name, "name");
        if (errName != null) {
            return Result.failure(errName);
        }
        var locationResult = Location.create(coordinate_x, coordinate_y);
        if (locationResult.isFailure()) {
            return Result.failure(locationResult.getError());
        }
        return Result.success(new CreateCourierCommand(name, locationResult.getValue()));
    }
}
