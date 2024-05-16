package ru.example.operator.gateway.controller;

import io.javaoperatorsdk.operator.api.reconciler.Cleaner;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.example.operator.common.configuration.OperatorProperties;
import ru.example.operator.gateway.cache.GatewayRouteCache;
import ru.example.operator.gateway.cache.GatewayRouteCacheEntry;
import ru.example.operator.gateway.customresource.gatewayroute.GatewayRoute;
import ru.example.operator.gateway.mapper.GatewayResourceMapper;

@Component
@ControllerConfiguration
@RequiredArgsConstructor
@Slf4j
public class GatewayRouteController implements Reconciler<GatewayRoute>, Cleaner<GatewayRoute> {

    private final OperatorProperties properties;

    private final GatewayResourceMapper mapper;

    private final GatewayRouteCache cache;

    @Override
    public UpdateControl<GatewayRoute> reconcile(final GatewayRoute gatewayRoute, final Context<GatewayRoute> context) throws Exception {
        var name = gatewayRoute.getMetadata().getName();
        var namespace = properties.getTargetService().getNamespace();
        var configMapName = properties.getTargetService().getConfigMap().getName();
        var spec = gatewayRoute.getSpec();

        var newRoute = mapper.mapToRoute(spec);

        log.info("Add custom resource GatewayRoute: {} to ConfigMap: {} in Namespace: {}", name, configMapName, namespace);

        cache.put(
                GatewayRouteCacheEntry.builder()
                        .customResource(gatewayRoute)
                        .mappedCustomResource(newRoute)
                        .build()
        );

        return UpdateControl.noUpdate();
    }

    @Override
    public DeleteControl cleanup(GatewayRoute gatewayRoute, Context<GatewayRoute> context) {
        var name = gatewayRoute.getMetadata().getName();
        var namespace = properties.getTargetService().getNamespace();
        var configMapName = properties.getTargetService().getConfigMap().getName();

        log.info("Remove custom resource GatewayRoute: {} from ConfigMap: {} in Namespace: {}", name, configMapName, namespace);

        cache.remove(name);

        return DeleteControl.defaultDelete();
    }
}
