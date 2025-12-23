package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ProjectIdGenerator;

import java.util.UUID;

public class UuidProjectIdGenerator implements ProjectIdGenerator {

    @Override
    public ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

}
