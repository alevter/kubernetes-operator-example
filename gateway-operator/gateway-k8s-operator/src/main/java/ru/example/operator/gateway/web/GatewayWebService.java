package ru.example.operator.gateway.web;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.example.operator.common.configuration.OperatorProperties;

import java.time.Duration;

import static ru.example.operator.common.constants.OperatorConstants.ACTUATOR_REFRESH_ENDPOINT;

@Component
@Slf4j
public class GatewayWebService {

    private final WebClient webClient;

    private final OperatorProperties properties;

    public GatewayWebService(OperatorProperties properties) {
        this.webClient = WebClient.builder()
                .filter((request, next) -> {
                    return next.exchange(request).retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)).doAfterRetry((retrySignal) -> {
                        log.warn("WebClient gateway - retrying {}", retrySignal.totalRetries() + 1L);
                    }));
                })
                .build();
        this.properties = properties;
    }

    public Mono<ResponseEntity<Void>> refresh(Pod pod) {
        var podIp = pod.getStatus().getPodIP();
        var port = properties.getTargetService().getPort();

        log.info("Sending request to auth-gateway pod (Name: {}, Namespace: {}, IP: {}) for refresh config",
                pod.getMetadata().getName(), pod.getMetadata().getNamespace(), podIp);

        return webClient
                .mutate()
                .baseUrl("http://%s:%s".formatted(podIp, port))
                .build()
                .post()
                .uri(ACTUATOR_REFRESH_ENDPOINT)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> log.error("Error while sending request for refresh config in pod", e));
    }
}
