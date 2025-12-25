package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidProjectIdGenerator implements ProjectIdGenerator {

    @Override
    public ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

}
