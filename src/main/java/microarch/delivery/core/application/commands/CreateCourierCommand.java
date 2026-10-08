package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCourierCommand {
    private final String name;
    private final Location location;

    public static Result<CreateCourierCommand, Error> create(String name) {
        var errName = Guard.againstNullOrEmpty(name, "name");
        if (errName != null) {
            return Result.failure(errName);
        }
        var location = Location.generateRandomLocation();
        return Result.success(new CreateCourierCommand(name, location));
    }
}
