package ru.example.operator.common.cache.configmap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.fabric8.kubernetes.api.model.ConfigMap;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.example.operator.common.configuration.OperatorProperties;
import ru.example.operator.common.constants.OperatorConstants;
import ru.example.operator.common.service.KubernetesResourceService;

@Component
@RequiredArgsConstructor
@Slf4j
public class BaseConfigMapCache {

    protected final KubernetesResourceService kubernetesResourceService;

    private final OperatorProperties properties;

    private final Cache<String, ConfigMap> cache = CacheBuilder.newBuilder().build();

    @PostConstruct
    private void init() {
        var baseConfigMap = get();

        if (baseConfigMap != null) {
            log.debug("Base ConfigMap: {}", baseConfigMap);
        } else {
            var name = properties.getTargetService().getConfigMap().getName() + OperatorConstants.BASE_CONFIG_MAP_POSTFIX;
            var namespace = properties.getTargetService().getNamespace();
            log.warn("Base ConfigMap {} in namespace {} does not exist", name, namespace);
        }
    }

    public ConfigMap get() {
        var name = properties.getTargetService().getConfigMap().getName() + OperatorConstants.BASE_CONFIG_MAP_POSTFIX;
        var namespace = properties.getTargetService().getNamespace();
        var baseConfigMap = cache.getIfPresent(name);

        if (baseConfigMap == null) {
            baseConfigMap = kubernetesResourceService.getConfigMap(name, namespace);
        }

        return baseConfigMap;
    }

    public void put(@NonNull ConfigMap actual) {
        log.debug("Update base ConfigMap in cache");
        cache.put(actual.getMetadata().getName(), actual);
    }
}
