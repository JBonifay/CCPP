package io.joffrey.ccpp.projectplanning.infrastructure.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final Map<String, List<DomainEvent>> streams = new ConcurrentHashMap<>();

    @Override
    public void append(String streamId, List<DomainEvent> events, int expectedVersion) {
        streams.computeIfAbsent(streamId, k -> new ArrayList<>()).addAll(events);
    }

    @Override
    public List<DomainEvent> readStream(String streamId) {
        return new ArrayList<>(streams.getOrDefault(streamId, List.of()));
    }

    @Override
    public List<DomainEvent> readAllEvents() {
        return streams.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}
