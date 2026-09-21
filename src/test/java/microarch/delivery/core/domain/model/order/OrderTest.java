package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Assignment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    @Test
    void shouldBeCorrectWhenParamsAreCorrect() {
        var basketId = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(5);

        var result = Order.create(basketId, deliveryLocation, volume);

        assertThat(result.isSuccess()).isTrue();
        var order = result.getValue();
        assertThat(order.getId()).isEqualTo(basketId);
        assertThat(order.getVolume()).isEqualTo(volume);
        assertThat(order.getDeliveryLocation()).isEqualTo(deliveryLocation);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    void shouldBeErrorToAssignOrderIfStatusIsNotCreated() {
        var basketId = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(5);

        var result = Order.create(basketId, deliveryLocation, volume);
        var order = result.getValue();
        order.assignOrder();

        //Now status is assigned try to Assigned again
        assertThat(order.getStatus() == OrderStatus.Assigned).isTrue();
        var errToAssign = order.assignOrder();
        assertThat(errToAssign.isFailure()).isTrue();

        order.completeOrder();

        ////Now status is Completed try to assign again
        assertThat(order.getStatus() == OrderStatus.Completed).isTrue();
        var errToAssign2 = order.assignOrder();
        assertThat(errToAssign2.isFailure()).isTrue();
    }


    @Test
    void shouldBeErrorToCompleteOrderIfStatusIsNotAssigned() {
        var basketId = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(5);

        var result = Order.create(basketId, deliveryLocation, volume);
        var order = result.getValue();

        //Now status is Created try to assign again
        assertThat(order.getStatus() == OrderStatus.Created).isTrue();
        var errToAssign = order.completeOrder();
        assertThat(errToAssign.isFailure()).isTrue();

        order.assignOrder();
        order.completeOrder();

        ////Now status is Completed try to assign again
        assertThat(order.getStatus() == OrderStatus.Completed).isTrue();
        var errToAssign2 = order.completeOrder();
        assertThat(errToAssign2.isFailure()).isTrue();
    }

    @Test
    void shouldBeTrueWhenChangeStatusCorrectly() {
        var basketId = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(5);

        var result = Order.create(basketId, deliveryLocation, volume);
        var order = result.getValue();

        //Now status is Created try to assign again
        assertThat(order.getStatus() == OrderStatus.Created).isTrue();
        var assignOkRes = order.assignOrder();
        assertThat(assignOkRes.isSuccess()).isTrue();


        ////Now status is Assigned try to assign again
        assertThat(order.getStatus() == OrderStatus.Assigned).isTrue();
        var completeOkRes = order.completeOrder();
        assertThat(completeOkRes.isSuccess()).isTrue();
    }

    @Test
    void shouldBeDifferentTwoCreatedOrdersWithDifferentId() {
        var basketId1 = UUID.randomUUID();
        var basketId2 = UUID.randomUUID();
        var deliveryLocation = Location.mustCreate(5, 5);
        var volume = Volume.mustCreate(5);

        var result1 = Order.create(basketId1, deliveryLocation, volume);
        var result2 = Order.create(basketId2, deliveryLocation, volume);
        var order1 = result1.getValue();
        var order2 = result2.getValue();

        assertThat(order1.equals(order2)).isFalse();
    }
}
