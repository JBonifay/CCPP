package fr.joffreybonifay.ccpp.workspace.infrastructure.spi;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.domain.WorkspaceIdGenerator;

import java.util.UUID;

public class UuidWorkspaceIdGenerator implements WorkspaceIdGenerator {

    @Override
    public WorkspaceId generate() {
        return new WorkspaceId(UUID.randomUUID());
    }

}
