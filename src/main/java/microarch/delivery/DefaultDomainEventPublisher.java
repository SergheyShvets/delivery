package microarch.delivery;

import libs.ddd.Aggregate;
import libs.ddd.DomainEvent;
import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.ports.DomainEventProducer;
import org.springframework.stereotype.Component;

@Component
public class DefaultDomainEventPublisher implements DomainEventPublisher {
    private final DomainEventProducer producer;

    public DefaultDomainEventPublisher(DomainEventProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(Iterable<? extends Aggregate<?>> aggregates) {
        for (Aggregate<?> aggregate : aggregates) {
            for (DomainEvent event : aggregate.getDomainEvents()) {
                producer.produce(event);
            }
            aggregate.clearDomainEvents();
        }
    }
}