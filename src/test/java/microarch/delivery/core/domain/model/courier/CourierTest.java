package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CourierTest {

    @Test
    void shouldBeCorrectWhenParamsAreCorrect() {
        var name = "Ivan";
        var location = Location.mustCreate(4, 5);

        var result = Courier.create(name, location);

        assertThat(result.isSuccess()).isTrue();
        var courier = result.getValue();
        assertThat(courier.getName()).isEqualTo(name);
        assertThat(courier.getLocation()).isEqualTo(location);
    }

    @Test
    void shouldBeAddOrderCorrectly() {
        var order = createOrder(5);

        var name = "Ivan";
        var location = Location.mustCreate(1, 1);

        var result = Courier.create(name, location);
        var courier = result.getValue();

        var addOrderRes = addOrder(courier, order);

        assertThat(addOrderRes.isSuccess()).isTrue();
    }

    @Test
    void shouldBeErrorAddOrderIfIsFull() {
        //Limit 20
        var order1 = createOrder(5);
        var order2 = createOrder(16);

        var name = "Ivan";
        var location = Location.mustCreate(1, 1);

        var result = Courier.create(name, location);
        var courier = result.getValue();

        //Add first order
        var addOrderRes = addOrder(courier, order1);
        assertThat(addOrderRes.isSuccess()).isTrue();
        assertThat(courier.getAssignments().length).isEqualTo(1);

        //Not added second order because is more than max courier volume
        var errAddOrderRes = addOrder(courier, order2);
        assertThat(errAddOrderRes.isFailure()).isTrue();
        assertThat(courier.getAssignments().length).isNotEqualTo(2);
    }


    @Test
    void shouldBeErrorToCloseAssignedIfIsFar() {
        // coordinate 5,5
        var order = createOrder(5);

        var name = "Ivan";
        var location = Location.mustCreate(1, 1);

        var result = Courier.create(name, location);
        var courier = result.getValue();

        addOrder(courier, order);
        var errCloseAssigned = courier.closeAssigned(order.getId());

        assertThat(errCloseAssigned.isFailure()).isTrue();
    }

    @Test
    void shouldBeSuccessToCloseAssigned() {
        // coordinate 5,5
        var order = createOrder(5);

        var name = "Ivan";
        var location = Location.mustCreate(4, 5);

        var result = Courier.create(name, location);
        var courier = result.getValue();

        addOrder(courier, order);
        var errCloseAssigned = courier.closeAssigned(order.getId());

        assertThat(errCloseAssigned.isSuccess()).isTrue();
    }

    @Test
    void shouldBeErrorToMoveMoreThenLimit() {
        var name = "Ivan";
        var location = Location.mustCreate(1, 1);
        var farLocation = Location.mustCreate(2, 2);

        var result = Courier.create(name, location);
        var courier = result.getValue();

        var errCloseAssigned = courier.setNewLocation(farLocation.getCoordinate_x(), farLocation.getCoordinate_y());
        assertThat(errCloseAssigned.isFailure()).isTrue();
    }

    @Test
    void shouldReturnToStartPositionIfMoveInPositionSuccess() {
        // coordinate 5,5

        var name = "Ivan";
        var location = Location.mustCreate(5, 5);

        var result = Courier.create(name, location);
        var courier = result.getValue();
        moveUp(courier);
        moveDown(courier);
        moveLeft(courier);
        moveRight(courier);

        assertThat(courier.getLocation()).isEqualTo(location);
    }


    @Test
    void shouldMoveToOrderToCloseAssign() {
        // coordinate 5,5
        var order = createOrder(5);

        var name = "Ivan";
        var location = Location.mustCreate(1, 1);

        var result = Courier.create(name, location);
        var courier = result.getValue();
        addOrder(courier, order);

        //In Start courier 1,1 is far from assign
        var errCloseAssigned = courier.closeAssigned(order.getId());
        assertThat(errCloseAssigned.isFailure()).isTrue();

        moveUp(courier);
        moveUp(courier);
        moveUp(courier);
        moveUp(courier);
        moveRight(courier);
        moveRight(courier);
        moveRight(courier);
        moveRight(courier);
        var closeAssignRes = courier.closeAssigned(order.getId());
        assertThat(courier.getLocation()).isEqualTo(order.getDeliveryLocation());
        assertThat(closeAssignRes.isSuccess()).isTrue();
    }

    private Order createOrder(int volumeValue) {
        var basketId = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(volumeValue);

        var resultOrder = Order.create(basketId, deliveryLocation, volume);
        return resultOrder.getValue();
    }

    private UnitResult<Error> addOrder(Courier courier, Order order) {
        return courier.addOrder(order.getId(), order.getVolume(), order.getDeliveryLocation());
    }

    private UnitResult<libs.errs.Error> moveUp(Courier courier) {
        return courier.setNewLocation(courier.getLocation().getCoordinate_x(), courier.getLocation().getCoordinate_y() + 1);
    }

    private UnitResult<libs.errs.Error> moveDown(Courier courier) {
        return courier.setNewLocation(courier.getLocation().getCoordinate_x(), courier.getLocation().getCoordinate_y() - 1);
    }

    private UnitResult<libs.errs.Error> moveLeft(Courier courier) {
        return courier.setNewLocation(courier.getLocation().getCoordinate_x() - 1, courier.getLocation().getCoordinate_y());

    }

    private UnitResult<Error> moveRight(Courier courier) {
        return courier.setNewLocation(courier.getLocation().getCoordinate_x() + 1, courier.getLocation().getCoordinate_y());
    }
}
