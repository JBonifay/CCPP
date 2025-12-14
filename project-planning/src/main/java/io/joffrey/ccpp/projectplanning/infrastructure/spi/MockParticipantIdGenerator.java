package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import io.joffrey.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.Setter;

import java.util.UUID;

@Setter
public class MockParticipantIdGenerator implements ParticipantIdGenerator {

    private ParticipantId mock;

    public MockParticipantIdGenerator() {
        this.mock = new ParticipantId(UUID.randomUUID());
    }

    @Override
    public ParticipantId generate() {
        return mock;
    }

    public String getValue() {
        return mock.value().toString();
    }
}
