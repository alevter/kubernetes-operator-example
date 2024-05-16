package ru.example.operator.common.service;

import io.javaoperatorsdk.operator.Operator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.example.operator.common.configuration.OperatorProperties;
import ru.example.operator.common.event.WatchedNamespacesEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WatchedNamespacesService {

    private final Operator operator;

    private final KubernetesResourceService kubernetesResourceService;

    private final WatchedNamespacesHelper watchedNamespacesHelper;

    private final ApplicationEventPublisher eventPublisher;

    private final OperatorProperties properties;

    @Scheduled(
            initialDelayString = "${ru.example.operator.watched-namespaces.refresh.initial-delay:PT1M}",
            fixedDelayString = "${ru.example.operator.watched-namespaces.refresh.delay:PT30S}"
    )
    public Set<String> refresh() {
        var labelSelector = String.join(
                "=",
                properties.getWatchedNamespaces().getLabel().getName(),
                properties.getWatchedNamespaces().getLabel().getValue()
        );
        var namespaces = kubernetesResourceService.getNamespaces(labelSelector);
        refreshWatchedNamespaces(namespaces);
        return namespaces;
    }

    private void refreshWatchedNamespaces(Set<String> namespaces) {
        if (watchedNamespacesHelper.isNamespacesChanged(namespaces)) {
            log.info("Set of namespaces for watching resources has changed to the following set: {}", namespaces);
            operator.getRegisteredControllers().forEach(controller -> controller.changeNamespaces(namespaces));
            eventPublisher.publishEvent(new WatchedNamespacesEvent(namespaces));
        }
    }
}

