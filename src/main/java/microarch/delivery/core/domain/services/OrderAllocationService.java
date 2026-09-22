package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

public interface OrderAllocationService {

    UnitResult<Error> allocateOrder(Order order, Courier... couriers);
}
