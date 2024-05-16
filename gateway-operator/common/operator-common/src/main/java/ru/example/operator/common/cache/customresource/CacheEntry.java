package ru.example.operator.common.cache.customresource;

import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class CacheEntry<T extends CustomResource<?, ?>, S> {

    private T customResource;

    private S mappedCustomResource;
}
