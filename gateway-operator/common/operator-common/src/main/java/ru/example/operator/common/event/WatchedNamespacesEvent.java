package ru.example.operator.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
public class WatchedNamespacesEvent extends ApplicationEvent {

    private final Set<String> namespaces;

    public WatchedNamespacesEvent(Set<String> namespaces) {
        super(namespaces);
        this.namespaces = namespaces;
    }
}