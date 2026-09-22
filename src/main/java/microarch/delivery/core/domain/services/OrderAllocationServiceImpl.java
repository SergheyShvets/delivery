package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;


public class OrderAllocationServiceImpl implements OrderAllocationService {

    @Override
    public UnitResult<Error> allocateOrder(Order order, Courier... couriers) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(couriers, "couriers");
        if (couriers.length == 0) return UnitResult.failure(GeneralErrors.valueIsRequired("courier list is empty"));

        var sortedCouriers = sortCouriersByDistant(order, couriers);
        for (Courier sortedCourier : sortedCouriers) {
            var addOrderRes = sortedCourier.addOrder(order.getId(), order.getVolume(), order.getDeliveryLocation());
            if (addOrderRes.isSuccess()) {
                return order.assignOrder();
            }
        }

        return UnitResult.failure(GeneralErrors.notFound("Courier for order not found", order.getId()));
    }

    private Courier[] sortCouriersByDistant(Order order, Courier... couriers) {
        var deliveryLocation = order.getDeliveryLocation();
        return Arrays.stream(couriers).sorted(Comparator.comparingInt((Courier c) -> deliveryLocation.countStepsTo(c.getLocation()))).toArray(Courier[]::new);
    }
}
