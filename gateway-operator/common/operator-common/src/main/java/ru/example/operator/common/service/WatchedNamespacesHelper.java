package ru.example.operator.common.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.example.operator.common.event.WatchedNamespacesEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WatchedNamespacesHelper implements ApplicationListener<WatchedNamespacesEvent> {

    /**
     * Кэш namespaces, по которым операторы наблюдают ресурсы в текущий момент времени.
     * Используется для проверки в {@link WatchedNamespacesService} изменения списка namespaces в кластере,
     * имеющих определённый label.
     * Необходим, т.к. Operator SDK показывает namespaces, указанные при регистрации контроллеров.
     */
    @Getter
    private final Set<String> cachedWatchedNamespaces = ConcurrentHashMap.newKeySet();

    @Override
    public void onApplicationEvent(WatchedNamespacesEvent event) {
        var namespaces = event.getNamespaces();

        if (isNamespacesChanged(namespaces)) {
            cachedWatchedNamespaces.clear();
            cachedWatchedNamespaces.addAll(namespaces);
        }
    }

    public boolean isNamespacesChanged(Set<String> namespaces) {
        return cachedWatchedNamespaces.size() != namespaces.size() || !cachedWatchedNamespaces.containsAll(namespaces);
    }
}
