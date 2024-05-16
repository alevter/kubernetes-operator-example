package ru.example.operator.gateway.customresource.microfrontend;

import lombok.Data;

import java.util.List;

@Data
public class MicrofrontendSpec {

    private String name;
    private String path;
    private String url;
    private String filename;
    private String scope;
    private String module;
    private List<WidgetSpec> widgets;

    @Data
    public static class WidgetSpec {
        private String name;
        private String module;
    }
}
