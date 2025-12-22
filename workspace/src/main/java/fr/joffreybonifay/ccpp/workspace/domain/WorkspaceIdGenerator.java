package fr.joffreybonifay.ccpp.workspace.domain;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public interface WorkspaceIdGenerator {
    WorkspaceId generate();
}
