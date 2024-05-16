package ru.example.operator.gateway.cache;

import lombok.experimental.SuperBuilder;
import ru.example.operator.common.cache.customresource.CacheEntry;
import ru.example.operator.gateway.customresource.gatewayroute.GatewayRoute;

import static ru.example.operator.gateway.entity.ConfigMapData.Route;

@SuperBuilder
public class GatewayRouteCacheEntry extends CacheEntry<GatewayRoute, Route> {
}
