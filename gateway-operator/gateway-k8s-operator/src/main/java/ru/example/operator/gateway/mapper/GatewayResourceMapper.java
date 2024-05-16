package ru.example.operator.gateway.mapper;

import org.mapstruct.Mapper;
import ru.example.operator.gateway.customresource.gatewayroute.GatewayRouteSpec;
import ru.example.operator.gateway.customresource.microfrontend.MicrofrontendSpec;

import static ru.example.operator.gateway.entity.ConfigMapData.Microfrontend;
import static ru.example.operator.gateway.entity.ConfigMapData.Route;

@Mapper(componentModel = "spring")
public interface GatewayResourceMapper {

    Route mapToRoute(GatewayRouteSpec spec);

    Microfrontend mapToMicrofrontend(MicrofrontendSpec spec);
}
