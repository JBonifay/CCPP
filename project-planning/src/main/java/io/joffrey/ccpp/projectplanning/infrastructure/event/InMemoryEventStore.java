package io.joffrey.ccpp.projectplanning.infrastructure.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.exception.ConcurrencyException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<DomainEvent>> streams = new ConcurrentHashMap<>();

    @Override
    public void append(UUID streamId, List<DomainEvent> events, int expectedVersion) {
        synchronized (streams) {
            List<DomainEvent> stream = streams.getOrDefault(streamId, new ArrayList<>());
            int currentVersion = stream.size() - 1;

            if (expectedVersion != -1 && currentVersion != expectedVersion) {
                throw new ConcurrencyException(
                        String.format("Concurrency conflict for stream '%s': expected version %d but current version is %d",
                                streamId, expectedVersion, currentVersion)
                );
            }

            stream.addAll(events);
            streams.put(streamId, stream);
        }
    }

    @Override
    public List<DomainEvent> readStream(UUID streamId) {
        return new ArrayList<>(streams.getOrDefault(streamId, List.of()));
    }

    @Override
    public List<DomainEvent> readAllEvents() {
        return streams.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}
