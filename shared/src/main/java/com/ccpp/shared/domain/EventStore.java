package com.ccpp.shared.domain;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void append(UUID streamId, List<Object> events, int expectedVersion);
    List<Object> readStream(UUID streamId);
}
