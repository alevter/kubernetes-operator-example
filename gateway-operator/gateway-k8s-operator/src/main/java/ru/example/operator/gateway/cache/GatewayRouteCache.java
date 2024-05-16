package ru.example.operator.gateway.cache;

import org.springframework.stereotype.Component;
import ru.example.operator.common.cache.customresource.CustomResourceCache;

import java.util.List;
import java.util.Map;

import static ru.example.operator.gateway.entity.ConfigMapData.Route;

@Component
public class GatewayRouteCache extends CustomResourceCache<GatewayRouteCacheEntry> {

    public List<Route> getRoutes() {
        return getCache().asMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue().getMappedCustomResource())
                .toList();
    }
}
