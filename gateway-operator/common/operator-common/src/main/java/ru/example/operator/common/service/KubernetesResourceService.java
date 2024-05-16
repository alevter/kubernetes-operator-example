package ru.example.operator.common.service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesResourceService {

    private final KubernetesClient kubernetesClient;

    public Set<String> getNamespaces(String labelSelector) {
        return kubernetesClient.namespaces()
                .withLabelSelector(labelSelector)
                .list()
                .getItems()
                .stream()
                .filter(Objects::nonNull)
                .map(namespace -> namespace.getMetadata().getName())
                .collect(Collectors.toSet());
    }

    public ConfigMap getConfigMap(String name, String namespace) {
        return kubernetesClient.configMaps()
                .inNamespace(namespace)
                .withName(name)
                .get();
    }

    public void updateConfigMap(ConfigMap configMap) {
        var namespace = configMap.getMetadata().getNamespace();

        log.info("Update ConfigMap: {} in Namespace: {}", configMap.getMetadata().getName(), namespace);

        kubernetesClient.configMaps()
                .inNamespace(namespace)
                .resource(configMap)
                .lockResourceVersion()
                .update();
    }

    public List<Pod> getPods(String namespace, String labelSelector) {
        return kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabelSelector(labelSelector)
                .list()
                .getItems();
    }

    public void executeCommandInPod(String command, Pod pod) {
        var podName = pod.getMetadata().getName();
        var podNamespace = pod.getMetadata().getNamespace();

        try (
                var execWatch = kubernetesClient.pods()
                        .inNamespace(podNamespace)
                        .withName(podName)
                        .redirectingOutput()
                        .exec(command)
        ) {
            execWatch.exitCode().join();
        } catch (Exception e) {
            log.error("Error while execute command \"{}\" in Pod: {} in Namespace: {}", command, podName, podNamespace, e);
        }
    }
}

