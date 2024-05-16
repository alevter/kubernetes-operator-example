package ru.example.operator.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "ru.example.operator")
public class OperatorProperties {

    private WatchedCr watchedCr;

    private WatchedNamespaces watchedNamespaces;

    private TargetServiceProperties targetService;

    @Data
    public static class WatchedCr {
        private LabelProperties label;
    }

    @Data
    public static class WatchedNamespaces {
        private LabelProperties label;
        private RefreshProperties refresh;
    }

    @Data
    public static class LabelProperties {
        private String name;
        private String value;
    }

    @Data
    public static class RefreshProperties {
        private boolean enabled = true;
        private Duration initialDelay;
        private Duration delay;
    }

    @Data
    public static class TargetServiceProperties {
        private String namespace;
        private Integer port = 8080;
        private PodProperties pod;
        private ConfigMapProperties configMap;
    }

    @Data
    public static class PodProperties {
        private LabelProperties label;
        private RefreshProperties refresh;
    }

    @Data
    public static class ConfigMapProperties {
        private String name;
        private RefreshProperties refresh;
    }
}
