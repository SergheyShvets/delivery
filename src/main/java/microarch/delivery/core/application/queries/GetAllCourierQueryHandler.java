package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;

public interface GetAllCourierQueryHandler {
    Result<GetAllCourierResponse[], Error> handle();
}
