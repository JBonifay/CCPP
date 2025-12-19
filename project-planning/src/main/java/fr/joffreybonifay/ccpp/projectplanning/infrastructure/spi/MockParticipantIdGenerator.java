package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
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
