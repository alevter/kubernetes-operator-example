package ru.example.operator.common.watcher;

import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.example.operator.common.configuration.OperatorProperties;
import ru.example.operator.common.constants.OperatorConstants;

@Component
@RequiredArgsConstructor
public class BaseConfigMapHandler {

    private final KubernetesClient kubernetesClient;

    private final BaseConfigMapWatcher watcher;

    private final OperatorProperties properties;

    @PostConstruct
    private void watchChanges() {
        kubernetesClient.configMaps()
                .inNamespace(properties.getTargetService().getNamespace())
                .withName(properties.getTargetService().getConfigMap().getName() + OperatorConstants.BASE_CONFIG_MAP_POSTFIX)
                .watch(watcher);
    }
}
