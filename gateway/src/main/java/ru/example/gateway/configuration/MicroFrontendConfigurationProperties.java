package ru.example.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "ru.example")
@RefreshScope
public class MicroFrontendConfigurationProperties {

    private List<MicroFrontendEntity> microfrontends = new ArrayList<>();

    @Data
    public static class MicroFrontendEntity {
        private String name;
        private String path;
        private String url;
        private String filename;
        private String scope;
        private String module;
        private List<MicroFrontendWidgetEntity> widgets;

        @Data
        public static class MicroFrontendWidgetEntity {
            private String name;
            private String module;
        }
    }
}
