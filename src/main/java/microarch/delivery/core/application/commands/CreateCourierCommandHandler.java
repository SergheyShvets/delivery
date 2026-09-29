package microarch.delivery.core.application.commands;

import libs.errs.Result;
import libs.errs.Error;

import java.util.UUID;

public interface CreateCourierCommandHandler {
    Result<UUID, Error> handle(CreateCourierCommand command);
}
