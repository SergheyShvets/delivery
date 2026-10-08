package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

public record GetNotCompletedOrdersResponse(UUID orderId, int coordinate_x, int coordinate_y) {
}
