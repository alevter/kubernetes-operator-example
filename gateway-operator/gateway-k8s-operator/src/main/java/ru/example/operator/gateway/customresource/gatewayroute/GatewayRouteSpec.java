package ru.example.operator.gateway.customresource.gatewayroute;

import lombok.Data;

import java.util.List;

@Data
public class GatewayRouteSpec {

    private String id;
    private String uri;
    private List<String> predicates;
    private List<String> filters;
}
