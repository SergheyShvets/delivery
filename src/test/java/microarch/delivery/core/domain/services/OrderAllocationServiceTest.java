package microarch.delivery.core.domain.services;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderAllocationServiceTest {
    @Test
    void shouldBeErrorIfCourierListIsEmpty() {
        var allocatedService = new OrderAllocationServiceImpl();
        Order order = createOrder(Location.mustCreate(5, 5), Volume.mustCreate(5));
        Courier[] emptyCourierList = new Courier[]{};

        var result = allocatedService.allocateOrder(order, emptyCourierList);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldBeErrorIfOrderStatusIsNotCreated() {
        var allocatedService = new OrderAllocationServiceImpl();
        Order order = createOrder(Location.mustCreate(5, 5), Volume.mustCreate(5));
        Courier[] couriers = createRandomCouriersByLocation(
                Location.mustCreate(5, 5),
                Location.mustCreate(5, 4)
        );

        // change status for order
        var assignOrderRes = order.assignOrder();
        assertThat(assignOrderRes.isSuccess()).isTrue();


        var result = allocatedService.allocateOrder(order, couriers);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldBeErrorIfCouriersDontHaveVolume() {
        var allocatedService = new OrderAllocationServiceImpl();
        Order order = createOrder(Location.mustCreate(5, 5), Volume.mustCreate(5));
        Courier[] couriers = createRandomCouriersByLocation(
                Location.mustCreate(5, 5),
                Location.mustCreate(5, 4),
                Location.mustCreate(9, 9)
        );

        //couriers gets heavy orders
        for (Courier courier : couriers) {
           var  heavyOrder = createOrder(Location.mustCreate(5, 5), Volume.mustCreate(19));
           var addingHeavyOrderRes = courier.addOrder(heavyOrder.getId(),heavyOrder.getVolume(),heavyOrder.getDeliveryLocation());
            assertThat(addingHeavyOrderRes.isSuccess()).isTrue();
        }

        var result = allocatedService.allocateOrder(order, couriers);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldBeSuccessIfFindCourier() {
        var allocatedService = new OrderAllocationServiceImpl();
        Order order = createOrder(Location.mustCreate(5, 5), Volume.mustCreate(5));
        Courier[] couriers = createRandomCouriersByLocation(
                Location.mustCreate(1, 1),
                Location.mustCreate(5, 4),
                Location.mustCreate(9, 9)
        );

        var result = allocatedService.allocateOrder(order, couriers);
        assertThat(result.isSuccess()).isTrue();
        var courierWithOrder = couriers[1];
        assertThat(courierWithOrder.getAssignments().length == 1).isTrue();
        assertThat(order.getStatus() == OrderStatus.Assigned).isTrue();
    }


    private Order createOrder(Location deliveryLocation, Volume volume) {
        return Order.create(UUID.randomUUID(), deliveryLocation, volume).getValueOrThrow();
    }

    private Courier[] createRandomCouriersByLocation(Location... locations) {
        return Arrays.stream(locations).map(
                a -> Courier.create(someNames[ThreadLocalRandom.current().nextInt(someNames.length)], a).getValueOrThrow()
        ).toArray(Courier[]::new);
    }

    private static String[] someNames = new String[]{"Иван", "Олег", "Виктор", "Петя", "Настя", "Дима"};
}
