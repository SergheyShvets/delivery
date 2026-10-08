package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);

    void update(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> getOneWithStateCreated();

    List<Order> findAllByStatusIn(Collection<OrderStatus> statuses);
}
