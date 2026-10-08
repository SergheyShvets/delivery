package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.Address;
import microarch.delivery.core.domain.model.Location;

public interface GeoClient {
    Location getLocation(Address address);
}
