package io.joffrey.ccpp.projectplanning.infrastructure.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.domain.StoredEvent;
import com.ccpp.shared.exception.ConcurrencyException;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<StoredEvent>> streams = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryEventStore() {
        this.clock = Clock.systemUTC();
    }

    public InMemoryEventStore(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void append(UUID streamId, List<DomainEvent> events, int expectedVersion) {
        synchronized (streams) {
            var stream = streams.getOrDefault(streamId, new ArrayList<>());
            int currentVersion = stream.size() - 1;

            if (expectedVersion != -1 && currentVersion != expectedVersion) {
                throw new ConcurrencyException(
                        String.format("Concurrency conflict for stream '%s': expected version %d but current version is %d",
                                streamId, expectedVersion, currentVersion)
                );
            }

            var timestamp = clock.instant();
            int version = currentVersion;

            for (DomainEvent event : events) {
                version++;
                stream.add(new StoredEvent(
                    UUID.randomUUID(),
                    streamId,
                    version,
                    timestamp,
                    event
                ));
            }

            streams.put(streamId, stream);
        }
    }

    @Override
    public List<DomainEvent> readStream(UUID streamId) {
        return streams.getOrDefault(streamId, List.of())
            .stream()
            .map(StoredEvent::payload)
            .toList();
    }

}
