package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class AssignmentTest {

    @Test
    void shouldBeCorrectWhenParamsAreCorrectOnCreated() {
        var orderId = UUID.randomUUID();
        var volume = Volume.mustCreate(5);
        var location = Location.mustCreate(5, 5);

        var result = Assignment.create(orderId, volume, location);

        assertThat(result.isSuccess()).isTrue();
        var assignment = result.getValue();
        assertThat(assignment.getOrderId()).isEqualTo(orderId);
        assertThat(assignment.getVolume()).isEqualTo(volume);
        assertThat(assignment.getLocation()).isEqualTo(location);
        assertThat(assignment.checkIfCompleted()).isFalse();
    }


    @Test
    void shouldBeTrueChangedStatusToComplete() {
        var assignment = mockResultCreateAssignment().getValue();

        assertThat(assignment.checkIfCompleted()).isFalse();

        var courierLocation = Location.mustCreate(4, 5);
        assignment.completeAssignment(courierLocation);
        assertThat(assignment.checkIfCompleted()).isTrue();
    }

    @Test
    void shouldBeTrueWhenTryChangeTwoTimesStatusToComplete() {
        var assignment = mockResultCreateAssignment().getValue();

        assertThat(assignment.checkIfCompleted()).isFalse();

        var courierLocation = Location.mustCreate(4, 5);
        assignment.completeAssignment(courierLocation);
        assertThat(assignment.checkIfCompleted()).isTrue();

        assignment.completeAssignment(courierLocation);
        assertThat(assignment.checkIfCompleted()).isTrue();
    }

    @Test
    void houldBeErrorWhenHasFarLocationToChangedStatusToComplete() {
        var assignment = mockResultCreateAssignment().getValue();

        assertThat(assignment.checkIfCompleted()).isFalse();

        var courierLocation = Location.mustCreate(4, 4);
        assignment.completeAssignment(courierLocation);
        assertThat(assignment.checkIfCompleted()).isFalse();
    }

    private Result<Assignment, Error> mockResultCreateAssignment() {
        var orderId = UUID.randomUUID();
        var volume = Volume.mustCreate(5);
        var location = Location.mustCreate(5, 5);

        return Assignment.create(orderId, volume, location);
    }
}
