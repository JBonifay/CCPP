package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import io.joffrey.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.util.UUID;

public class UuidParticipantIdGenerator implements ParticipantIdGenerator {

    @Override
    public ParticipantId generate() {
        return new ParticipantId(UUID.randomUUID());
    }

}
