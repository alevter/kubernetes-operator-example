package ru.example.operator.common.watcher;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.example.operator.common.cache.configmap.BaseConfigMapCache;

@Component
@RequiredArgsConstructor
@Slf4j
public class BaseConfigMapWatcher implements Watcher<ConfigMap> {

    private final BaseConfigMapCache cache;

    @Override
    public void eventReceived(Action action, ConfigMap configMap) {
        var name = configMap.getMetadata().getName();
        var namespace = configMap.getMetadata().getNamespace();

        switch (action) {
            case ADDED, MODIFIED -> {
                log.info("Base ConfigMap {} in namespace {} was {}", name, namespace, action);
                cache.put(configMap);
            }
            case DELETED -> {
                // TODO: Should we handle this case?
                log.warn("Base ConfigMap {} in namespace {} was {}", name, namespace, action);
            }
            default -> log.warn("Unknown or unhandled action {} on base ConfigMap {} in namespace {}", action, name, namespace);
        }
    }

    @Override
    public void onClose(WatcherException e) {
        log.error("Error while watching changes in base ConfigMap", e);
    }
}
