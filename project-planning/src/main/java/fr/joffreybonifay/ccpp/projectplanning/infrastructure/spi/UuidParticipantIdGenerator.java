package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.util.UUID;

public class UuidParticipantIdGenerator implements ParticipantIdGenerator {

    @Override
    public ParticipantId generate() {
        return new ParticipantId(UUID.randomUUID());
    }

}
