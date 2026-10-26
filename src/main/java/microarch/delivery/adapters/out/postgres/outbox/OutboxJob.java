package microarch.delivery.adapters.out.postgres.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import libs.ddd.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.ports.DomainEventProducer;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class OutboxJob implements Job {
    private final DomainEventProducer producer;
    private final OutboxJpaRepository jpa;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) {
        var outboxMessages = jpa.findUnprocessedMessages();
        for (var outboxMessage : outboxMessages) {
            try {
                var eventClass = Class.forName(outboxMessage.getEventType());
                var eventObject = objectMapper.readValue(outboxMessage.getPayload(), eventClass);

                if (!(eventObject instanceof DomainEvent domainEvent)) {
                    throw new IllegalStateException("Invalid outbox message type: " + eventClass);
                }

                producer.produce(domainEvent);

                outboxMessage.markAsProcessed();
                jpa.save(outboxMessage);
            } catch (Exception e) {
                log.error("Failed to publish outbox message {}", outboxMessage.getId(), e);
            }
        }
    }
}
