package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpa;

    public OrderRepositoryImpl(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Order order) {
        jpa.save(order);
    }

    @Override
    public boolean update(Order order) {
        if (jpa.existsById(order.getId())) {
            jpa.save(order);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Order> getOneWithStateCreated() {
        return jpa.findFirstByStatus(OrderStatus.Created);
    }

    @Override
    public List<Order> getAllWithStateAssigned() {
        return jpa.findAllByStatus(OrderStatus.Assigned);
    }
}
