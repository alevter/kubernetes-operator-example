package ru.example.operator.common.service;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import ru.example.operator.common.configuration.OperatorProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.example.operator.common.constants.OperatorConstants.BASE_CONFIG_MAP_POSTFIX;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractConfigMapRefreshService {

    protected final KubernetesResourceService kubernetesResourceService;

    protected final OperatorProperties properties;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Обновление ConfigMap целевого сервиса, в случае если её data отличается от data базовой ConfigMap.
     * Базовая ConfigMap создаётся в момент деплоя оператора.<p>
     * В случае, если целевая ConfigMap сервиса не существует, проверка пропускается.
     */
    @Scheduled(
            initialDelayString = "${ru.example.operator.target-service.config-map.refresh.initial-delay:PT30S}",
            fixedDelayString = "${ru.example.operator.target-service.config-map.refresh.delay:PT30S}"
    )
    public void refresh() {
        var name = properties.getTargetService().getConfigMap().getName();
        var baseName = name + BASE_CONFIG_MAP_POSTFIX;
        var namespace = properties.getTargetService().getNamespace();
        var configMap = kubernetesResourceService.getConfigMap(name, namespace);

        if (configMap == null) {
            log.warn(
                    "ConfigMap: {} does not exist in Namespace: {}. Skip comparison with base ConfigMap: {}.",
                    name,
                    namespace,
                    baseName
            );

            return;
        }

        var data = configMap.getData();
        var desiredData = getDesiredConfigMapData();

        if (!desiredData.equals(data)) {
            log.info("Data of ConfigMap: {} is different from desired data", name);
            configMap.setData(desiredData);
            kubernetesResourceService.updateConfigMap(configMap);
            if (properties.getTargetService().getPod().getRefresh().isEnabled()) {
                scheduleRefreshInPods();
            }
        }
    }

    protected abstract void refreshInPod(Pod pod);

    protected abstract Map<String, String> getDesiredConfigMapData();

    protected List<Pod> getPods() {
        var namespace = properties.getTargetService().getNamespace();

        var labelSelector = String.join(
                "=",
                properties.getTargetService().getPod().getLabel().getName(),
                properties.getTargetService().getPod().getLabel().getValue()
        );

        var pods = kubernetesResourceService.getPods(namespace, labelSelector);

        if (CollectionUtils.isEmpty(pods)) {
            log.warn("No pods found in Namespace: {} with LabelSelector: {}", namespace, labelSelector);
        }

        return pods;
    }

    private void scheduleRefreshInPods() {
        var refreshDelay = properties.getTargetService().getPod().getRefresh().getDelay();
        var refreshDelayInSeconds = TimeUnit.SECONDS.convert(refreshDelay);
        executor.schedule(this::refreshInPods, refreshDelayInSeconds, TimeUnit.SECONDS);
    }

    private void refreshInPods() {
        var pods = getPods();

        if (!CollectionUtils.isEmpty(pods)) {
            pods.forEach(this::refreshInPod);
        }
    }
}
