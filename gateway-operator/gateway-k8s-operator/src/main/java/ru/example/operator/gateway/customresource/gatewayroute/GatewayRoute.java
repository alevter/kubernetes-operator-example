package ru.example.operator.gateway.customresource.gatewayroute;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("operator.example.ru")
@Version("v1")
public class GatewayRoute extends CustomResource<GatewayRouteSpec, GatewayRouteStatus> implements Namespaced {
}
