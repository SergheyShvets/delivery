package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    OrderRepository repository;

    @Test
    void CanAddOrder() {
        var basketId = UUID.randomUUID();
        var deliveryLocaleResult = Location.create(5, 5).getValue();
        var volumeResult = Volume.create(5).getValue();
        var order = Order.create(basketId, deliveryLocaleResult, volumeResult).getValue();

        repository.save(order);
        var loaded = repository.findById(basketId);
        assertThat(loaded).isPresent();
        assertThat(loaded.get()).isEqualTo(order);
        assertThat(loaded.get().getStatus()).isEqualTo(order.getStatus());
    }

    @Test
    void CanGetByIdOrder() {
        var basketId = UUID.randomUUID();
        var deliveryLocaleResult = Location.create(5, 5).getValue();
        var volumeResult = Volume.create(5).getValue();
        var order = Order.create(basketId, deliveryLocaleResult, volumeResult).getValue();
        //Not added will return error
        var loadedEmpty = repository.findById(basketId);
        assertThat(loadedEmpty).isEmpty();

        repository.save(order);
        var loaded = repository.findById(basketId);
        assertThat(loaded).isPresent();
    }

}
