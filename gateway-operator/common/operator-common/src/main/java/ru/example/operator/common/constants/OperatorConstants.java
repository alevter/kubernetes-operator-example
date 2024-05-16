package ru.example.operator.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OperatorConstants {

    public static final String BASE_CONFIG_MAP_POSTFIX = "-base";

    public static final String CONFIG_NAME = "config.yml";

    public static final String ACTUATOR_REFRESH_ENDPOINT = "/actuator/refresh";
}

