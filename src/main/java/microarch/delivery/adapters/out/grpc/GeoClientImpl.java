package microarch.delivery.adapters.out.grpc;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import libs.errs.Error;
import jakarta.annotation.PreDestroy;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.ApplicationProperties;
import microarch.delivery.core.domain.model.Address;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.ports.GeoClient;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class GeoClientImpl implements GeoClient {
    private final int DURATION_SEC = 10;

    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub stub;

    public GeoClientImpl(ApplicationProperties properties) {
        this.channel = ManagedChannelBuilder.forAddress(properties.getGrpc().getGeoService().getHost(),
                properties.getGrpc().getGeoService().getPort()).usePlaintext().build();
        this.stub = GeoGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public Result<Location, Error> getLocation(Address address) {
        Objects.requireNonNull(address, "address");

        var request = GeoProto.GetGeolocationRequest.newBuilder().setStreet(address.getStreet()).build();
        try {
            var responceLocation = stub.withDeadlineAfter(DURATION_SEC, TimeUnit.SECONDS).getGeolocation(request).getLocation();
            return Result.success(Location.mustCreate(responceLocation.getX(), responceLocation.getY()));
        } catch (StatusRuntimeException ex) {
            return Result.failure(Error.of(ex.toString(), ex.getMessage()));
        }
    }
}
