package microarch.delivery.adapters.in.http.mapper;

import microarch.delivery.core.application.queries.GetAllCourierResponse;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeliveryMapper {
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    @Mapping(target = "id", source = "courierId")
    @Mapping(target = "location.x", source = "coordinate_x")
    @Mapping(target = "location.y", source = "coordinate_y")
    microarch.delivery.adapters.in.http.model.Courier toHttp(GetAllCourierResponse dto);

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "location.x", source = "coordinate_x")
    @Mapping(target = "location.y", source = "coordinate_y")
    microarch.delivery.adapters.in.http.model.Order toHttp(GetNotCompletedOrdersResponse dto);

}
