package ru.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.example.gateway.configuration.MicroFrontendConfigurationProperties;
import ru.example.gateway.controller.dto.ConfigurationResponseDto;
import ru.example.gateway.mapper.MicroFrontendMapper;

@RestController
@RequestMapping("/api/configuration")
@Slf4j
@RequiredArgsConstructor
public class ConfigController {

    private final MicroFrontendConfigurationProperties microFrontendProperties;

    private final MicroFrontendMapper microFrontendMapper;

    @GetMapping
    public Mono<ConfigurationResponseDto> getConfig() {
        var dto = ConfigurationResponseDto.builder()
                .microfrontends(microFrontendMapper.mapToListDto(microFrontendProperties.getMicrofrontends()))
                .build();

        return Mono.just(dto);
    }
}
