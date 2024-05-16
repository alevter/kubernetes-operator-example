package ru.example.gateway.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ConfigurationResponseDto {

    private List<MicroFrontendDto> microfrontends = new ArrayList<>();

    @Data
    public static class MicroFrontendDto {
        private String name;
        private String path;
        private String url;
        private String filename;
        private String scope;
        private String module;
        private List<MicroFrontendWidgetDto> widgets;

        @Data
        public static class MicroFrontendWidgetDto {
            private String name;
            private String module;
        }
    }
}
