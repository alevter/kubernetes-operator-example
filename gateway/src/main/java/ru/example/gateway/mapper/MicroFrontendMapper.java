package ru.example.gateway.mapper;

import org.mapstruct.Mapper;

import java.util.List;

import static ru.example.gateway.configuration.MicroFrontendConfigurationProperties.MicroFrontendEntity;
import static ru.example.gateway.controller.dto.ConfigurationResponseDto.MicroFrontendDto;

@Mapper(componentModel = "spring")
public interface MicroFrontendMapper {

    List<MicroFrontendDto> mapToListDto(List<MicroFrontendEntity> microFrontend);
}
