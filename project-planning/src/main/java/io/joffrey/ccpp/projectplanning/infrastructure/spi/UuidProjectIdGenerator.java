package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;

import java.util.UUID;

public class UuidProjectIdGenerator implements ProjectIdGenerator {

    @Override
    public ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

}
