package fr.joffreybonifay.ccpp.workspace.domain;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public interface WorkspaceIdGenerator {
    WorkspaceId generate();
}
