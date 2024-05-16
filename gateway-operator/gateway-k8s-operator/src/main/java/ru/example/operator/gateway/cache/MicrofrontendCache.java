package ru.example.operator.gateway.cache;

import org.springframework.stereotype.Component;
import ru.example.operator.common.cache.customresource.CustomResourceCache;

import java.util.List;
import java.util.Map;

import static ru.example.operator.gateway.entity.ConfigMapData.Microfrontend;

@Component
public class MicrofrontendCache extends CustomResourceCache<MicrofrontendCacheEntry> {

    public List<Microfrontend> getMicrofrontends() {
        return getCache().asMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue().getMappedCustomResource())
                .toList();
    }
}
