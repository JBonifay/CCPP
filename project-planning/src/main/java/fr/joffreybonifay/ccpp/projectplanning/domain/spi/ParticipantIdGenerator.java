package fr.joffreybonifay.ccpp.projectplanning.domain.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;

public interface ParticipantIdGenerator {
    ParticipantId generate();
}
