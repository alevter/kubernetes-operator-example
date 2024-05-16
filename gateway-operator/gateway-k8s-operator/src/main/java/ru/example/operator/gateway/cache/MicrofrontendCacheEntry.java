package ru.example.operator.gateway.cache;

import lombok.experimental.SuperBuilder;
import ru.example.operator.common.cache.customresource.CacheEntry;
import ru.example.operator.gateway.customresource.microfrontend.Microfrontend;
import ru.example.operator.gateway.entity.ConfigMapData;

@SuperBuilder
public class MicrofrontendCacheEntry extends CacheEntry<Microfrontend, ConfigMapData.Microfrontend> {
}
