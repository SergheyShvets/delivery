package microarch.delivery.adapters.out.kafka;

import libs.ddd.DomainEvent;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import queues.order.events.OrderEventsProto.OrderAssignedIntegrationEvent;
import queues.order.events.OrderEventsProto.OrderCompletedIntegrationEvent;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class KafkaDomainEventProducer implements DomainEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.order-events-topic}")
    private String topic;

    @Override
    public void produce(DomainEvent event) {
        switch (event) {
            case OrderAssignedDomainEvent e -> {
                var integrationEvent = mapToProto(e);
                sendAfterCommit(e.getOrderId().toString(), "OrderAssignedIntegrationEvent", integrationEvent.toByteArray());
            }
            case OrderCompletedDomainEvent e -> {
                var integrationEvent = mapToProto(e);
                sendAfterCommit(e.getOrderId().toString(), "OrderCompletedIntegrationEvent", integrationEvent.toByteArray());
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
        }
    }

    private void sendAfterCommit(String orderId, String eventType, byte[] payload) {
        var record = new ProducerRecord<String, byte[]>(topic, orderId, payload);
        record.headers().add("eventType", eventType.getBytes(StandardCharsets.UTF_8));
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(record);
                }
            });
        } else {
            send(record);
        }
    }

    private void send(ProducerRecord<String, byte[]> record) {
        try {
            kafkaTemplate.send(record).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publish interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }

    private OrderAssignedIntegrationEvent mapToProto(OrderAssignedDomainEvent event) {
        return OrderAssignedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }

    private OrderCompletedIntegrationEvent mapToProto(OrderCompletedDomainEvent event) {
        return OrderCompletedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }
}
