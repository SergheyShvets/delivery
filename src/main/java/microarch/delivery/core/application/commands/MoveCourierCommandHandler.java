package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;

import java.util.UUID;

public interface MoveCourierCommandHandler {
    UnitResult<Error> handle(MoveCourierCommand command);
}
