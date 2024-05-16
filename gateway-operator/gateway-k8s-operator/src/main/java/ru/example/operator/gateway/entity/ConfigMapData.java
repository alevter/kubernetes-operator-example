package ru.example.operator.gateway.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@JsonInclude(Include.NON_NULL)
public class ConfigMapData {

    private Ru ru;
    private Spring spring;

    public ConfigMapData(List<Microfrontend> microfrontends, List<Route> routes) {
        this.ru = Ru.builder().example(
                Example.builder().microfrontends(microfrontends).build()
        ).build();

        this.spring = Spring.builder().cloud(
                Cloud.builder().gateway(
                        Gateway.builder().routes(routes).build()
                ).build()
        ).build();
    }

    public ConfigMapData(List<Route> routes) {
        this.spring = Spring.builder().cloud(
                Cloud.builder().gateway(
                        Gateway.builder().routes(routes).build()
                ).build()
        ).build();
    }

    @Data
    @Builder
    private static class Ru {
        private Example example;
    }

    @Data
    @Builder
    private static class Example {
        private List<Microfrontend> microfrontends;
    }

    @Data
    public static class Microfrontend {
        private String name;
        private String path;
        private String url;
        private String filename;
        private String scope;
        private String module;
        private List<Widget> widgets;
    }

    @Data
    public static class Widget {
        private String name;
        private String module;
    }

    @Data
    @Builder
    private static class Spring {
        private Cloud cloud;
    }

    @Data
    @Builder
    private static class Cloud {
        private Gateway gateway;
    }

    @Data
    @Builder
    private static class Gateway {
        private List<Route> routes;
    }

    @Data
    public static class Route {
        private String id;
        private String uri;
        private List<String> predicates;
        private List<String> filters;
    }
}
