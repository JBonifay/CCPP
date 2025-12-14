package io.joffrey.ccpp.projectplanning.domain.spi;

import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public interface ParticipantIdGenerator {
    ParticipantId generate();
}
