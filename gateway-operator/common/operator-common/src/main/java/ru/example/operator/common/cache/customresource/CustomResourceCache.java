package ru.example.operator.common.cache.customresource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;

@Data
public abstract class CustomResourceCache<T extends CacheEntry<?,?>> {

    private final Cache<String, T> cache;

    protected CustomResourceCache() {
        this.cache = CacheBuilder.newBuilder().build();
    }

    public void put(T cacheEntry) {
        var name = cacheEntry.getCustomResource().getMetadata().getName();
        cache.put(name, cacheEntry);
    }

    public void remove(String customResourceName) {
        cache.asMap().remove(customResourceName);
    }
}
