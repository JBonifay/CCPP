package io.joffrey.ccpp.workspace.application.query.repository;

import com.ccpp.shared.domain.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;

import java.util.Optional;

public interface WorkspaceProjectCountReadRepository {
    void save(WorkspaceProjectCountDTO dto);

    Optional<WorkspaceProjectCountDTO> findById(WorkspaceId workspaceId);
}
