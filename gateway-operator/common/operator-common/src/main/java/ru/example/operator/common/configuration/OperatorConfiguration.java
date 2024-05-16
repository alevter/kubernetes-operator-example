package ru.example.operator.common.configuration;

import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.config.ControllerConfigurationOverrider;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.example.operator.common.event.WatchedNamespacesEvent;
import ru.example.operator.common.service.KubernetesResourceService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OperatorConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    private final OperatorProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @SuppressWarnings({"rawtypes","unchecked"})
    public Operator operator(List<Reconciler> controllers, KubernetesResourceService kubernetesResourceService) {
        var watchedNamespacesLabel = String.join(
                "=",
                properties.getWatchedNamespaces().getLabel().getName(),
                properties.getWatchedNamespaces().getLabel().getValue()
        );
        var namespaces = kubernetesResourceService.getNamespaces(watchedNamespacesLabel);

        var watchedCrLabel = String.join(
                "=",
                properties.getWatchedCr().getLabel().getName(),
                properties.getWatchedCr().getLabel().getValue()
        );

        var operator = new Operator();
        controllers.forEach(controller ->
                operator.register(
                        controller,
                        ControllerConfigurationOverrider.override(operator.getConfigurationService().getConfigurationFor(controller))
                                .settingNamespaces(namespaces)
                                .withLabelSelector(watchedCrLabel)
                                .build()
                )
        );
        operator.start();

        eventPublisher.publishEvent(new WatchedNamespacesEvent(namespaces));

        return operator;
    }
}

