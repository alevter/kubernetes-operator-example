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
import ru.example.operator.gateway.cache.MicrofrontendCache;
import ru.example.operator.gateway.cache.MicrofrontendCacheEntry;
import ru.example.operator.gateway.customresource.microfrontend.Microfrontend;
import ru.example.operator.gateway.mapper.GatewayResourceMapper;

@Component
@ControllerConfiguration
@RequiredArgsConstructor
@Slf4j
public class MicrofrontendController implements Reconciler<Microfrontend>, Cleaner<Microfrontend> {

    private final OperatorProperties properties;

    private final GatewayResourceMapper mapper;

    private final MicrofrontendCache cache;

    @Override
    public UpdateControl<Microfrontend> reconcile(Microfrontend microfrontend, Context<Microfrontend> context) throws Exception {
        var name = microfrontend.getMetadata().getName();
        var namespace = properties.getTargetService().getNamespace();
        var configMapName = properties.getTargetService().getConfigMap().getName();
        var spec = microfrontend.getSpec();

        var newMicrofrontend = mapper.mapToMicrofrontend(spec);

        log.info("Add custom resource Microfrontend: {} to ConfigMap: {} in Namespace: {}", name, configMapName, namespace);

        cache.put(
                MicrofrontendCacheEntry.builder()
                        .customResource(microfrontend)
                        .mappedCustomResource(newMicrofrontend)
                        .build()
        );

        return UpdateControl.noUpdate();
    }

    @Override
    public DeleteControl cleanup(Microfrontend microfrontend, Context<Microfrontend> context) {
        var name = microfrontend.getMetadata().getName();
        var namespace = properties.getTargetService().getNamespace();
        var configMapName = properties.getTargetService().getConfigMap().getName();

        log.info("Remove custom resource Microfrontend: {} from ConfigMap: {} in Namespace: {}", name, configMapName, namespace);

        cache.remove(name);

        return DeleteControl.defaultDelete();
    }
}
