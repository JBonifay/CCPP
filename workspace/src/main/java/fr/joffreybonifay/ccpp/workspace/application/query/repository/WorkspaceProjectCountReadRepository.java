package fr.joffreybonifay.ccpp.workspace.application.query.repository;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;

import java.util.Optional;

public interface WorkspaceProjectCountReadRepository {
    void save(WorkspaceProjectCountDTO dto);

    Optional<WorkspaceProjectCountDTO> findById(WorkspaceId workspaceId);
}
