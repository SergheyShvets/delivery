package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

public record GetAllCourierResponse(UUID courierId, String name, int coordinate_x, int coordinate_y) {
}
