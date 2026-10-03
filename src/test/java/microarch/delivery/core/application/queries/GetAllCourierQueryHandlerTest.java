package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class GetAllCourierQueryHandlerTest {
    private final CourierRepository courierRepository = mock(CourierRepository.class);

    @Test
    void GetAllCourierCommandHandler_ShouldBeSuccess_WhenParamsAreSuccess() {
        var couriers = new Courier[]{
                Courier.create("name", Location.create(1, 1).getValue()).getValue(),
                Courier.create("name1", Location.create(10, 10).getValue()).getValue(),
                Courier.create("name2", Location.create(4, 4).getValue()).getValue()
        };

        when(courierRepository.getAll()).thenReturn(Arrays.stream(couriers).toList());

        var handler = new GetAllCourierQueryHandlerImpl(courierRepository);
        var responseResult = handler.handle();

        assertThat(responseResult.isSuccess()).isTrue();
        var response = responseResult.getValue();
        assertThat(response.length).isEqualTo(couriers.length);
        for (int i = 0; i < response.length; i++) {
            assertThat(response[i].courierId()).isEqualTo(couriers[i].getId());
            assertThat(response[i].name()).isEqualTo(couriers[i].getName());
            assertThat(response[i].location()).isEqualTo(couriers[i].getLocation());
        }
    }
}
