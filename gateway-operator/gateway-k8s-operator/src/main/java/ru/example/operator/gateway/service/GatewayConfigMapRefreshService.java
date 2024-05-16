package ru.example.operator.gateway.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import ru.example.operator.common.cache.configmap.BaseConfigMapCache;
import ru.example.operator.common.configuration.OperatorProperties;
import ru.example.operator.common.service.AbstractConfigMapRefreshService;
import ru.example.operator.common.service.KubernetesResourceService;
import ru.example.operator.gateway.cache.GatewayRouteCache;
import ru.example.operator.gateway.cache.MicrofrontendCache;
import ru.example.operator.gateway.entity.ConfigMapData;
import ru.example.operator.gateway.web.GatewayWebService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ru.example.operator.common.constants.OperatorConstants.CONFIG_NAME;

@Component
@Slf4j
public class GatewayConfigMapRefreshService extends AbstractConfigMapRefreshService {

    private final GatewayWebService gatewayWebService;

    private final BaseConfigMapCache baseConfigMapCache;

    private final GatewayRouteCache gatewayRouteCache;

    private final MicrofrontendCache microfrontendCache;

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Autowired
    public GatewayConfigMapRefreshService(
            @NonNull final KubernetesResourceService kubernetesResourceService,
            @NonNull final OperatorProperties properties,
            @NonNull final GatewayWebService gatewayWebService,
            @NonNull final BaseConfigMapCache baseConfigMapCache,
            @NonNull final GatewayRouteCache gatewayRouteCache,
            @NonNull final MicrofrontendCache microfrontendCache
    ) {
        super(kubernetesResourceService, properties);
        this.gatewayWebService = gatewayWebService;
        this.baseConfigMapCache = baseConfigMapCache;
        this.gatewayRouteCache = gatewayRouteCache;
        this.microfrontendCache = microfrontendCache;
    }

    @Override
    protected void refreshInPod(Pod pod) {
        gatewayWebService.refresh(pod).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    @Override
    protected Map<String, String> getDesiredConfigMapData() {
        var baseConfigMap = baseConfigMapCache.get();
        var desiredConfigMapData = new HashMap<>(baseConfigMap.getData());
        var baseYaml = new ByteArrayResource(desiredConfigMapData.get(CONFIG_NAME).getBytes(StandardCharsets.UTF_8));

        var configMapData = new ConfigMapData(microfrontendCache.getMicrofrontends(), gatewayRouteCache.getRoutes());

        try {
            var customResourceYaml = new ByteArrayResource(objectMapper.writeValueAsString(configMapData).getBytes(StandardCharsets.UTF_8));

            var factory = new YamlMapFactoryBean();
            factory.setResources(baseYaml, customResourceYaml);
            var desiredConfigYaml = objectMapper.writeValueAsString(factory.getObject());

            desiredConfigMapData.put(CONFIG_NAME, desiredConfigYaml);

            return desiredConfigMapData;
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serialize ConfigMap: {}", baseConfigMap.getMetadata().getName(), e);
            throw new RuntimeException("Error occurred while serialize ConfigMap", e);
        }
    }
}