package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

public interface OrderAllocationService {

    Result<Courier, Error> allocateOrder(Order order, Courier... couriers);
}
