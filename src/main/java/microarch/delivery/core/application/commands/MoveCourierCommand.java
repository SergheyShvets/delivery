package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCourierCommand {
    private final UUID courierId;
    private final Location newLocation;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Location newLocation) {
        var errId = Guard.againstNullOrEmpty(courierId, "courierId");
        if (errId != null) {
            return Result.failure(errId);
        }
        if (newLocation == null) {
            return Result.failure(GeneralErrors.valueIsRequired("newLocation"));
        }
        return Result.success(new MoveCourierCommand(courierId, newLocation));
    }
}
