package microarch.delivery.adapters.in.http.mapper;

import microarch.delivery.core.application.queries.GetAllCourierResponse;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeliveryMapper {
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    microarch.delivery.adapters.in.http.model.Courier toHttp(GetAllCourierResponse dto);

    microarch.delivery.adapters.in.http.model.Order toHttp(GetNotCompletedOrdersResponse dto);

}
