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
public final class CompleteOrderCommand {
    private final UUID courierId;
    private final UUID orderId;

    public static Result<CompleteOrderCommand, Error> create(UUID courierId, UUID orderId) {
        var errCourierId = Guard.againstNullOrEmpty(courierId, "courierId");
        if (errCourierId != null) {
            return Result.failure(errCourierId);
        }
        var errOrderId = Guard.againstNullOrEmpty(orderId, "orderId");
        if (errOrderId != null) {
            return Result.failure(errOrderId);
        }
        return Result.success(new CompleteOrderCommand(courierId, orderId));
    }
}
