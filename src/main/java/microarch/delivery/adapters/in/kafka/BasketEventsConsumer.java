package microarch.delivery.adapters.in.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import queues.basket.events.BasketEventsProto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketEventsConsumer {
    private final CreateOrderCommandHandler createOrderCommandHandler;

    @KafkaListener(topics = "${app.kafka.basket-events-topic}")
    public void listen(byte[] message) {
        try {
            var event = BasketEventsProto.BasketConfirmedIntegrationEvent.parseFrom(message);
            var address = event.getAddress();
            var createOrderCommandResult = CreateOrderCommand.create(UUID.fromString(event.getBasketId()),
                    address.getCountry(), address.getCity(), address.getStreet(), address.getHouse(),
                    address.getApartment(), event.getVolume());
            if (createOrderCommandResult.isFailure()) {
                throw new RuntimeException("Invalid command: " + createOrderCommandResult.getError());
            }
            var handleCommandResult = createOrderCommandHandler.handle(createOrderCommandResult.getValue());
            if (handleCommandResult.isFailure()) {
                throw new RuntimeException("Failed to handle command: " + handleCommandResult.getError());
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Failed to parse protobuf message", e);
        }
    }
}
