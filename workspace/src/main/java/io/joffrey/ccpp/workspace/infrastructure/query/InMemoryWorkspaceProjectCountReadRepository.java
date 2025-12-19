package io.joffrey.ccpp.workspace.infrastructure.query;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import io.joffrey.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWorkspaceProjectCountReadRepository implements WorkspaceProjectCountReadRepository {

    private final Map<WorkspaceId, WorkspaceProjectCountDTO> workspaceProjectCountDTOs = new HashMap<>();

    @Override
    public void save(WorkspaceProjectCountDTO dto) {
        workspaceProjectCountDTOs.put(dto.workspaceId(), dto);
    }

    @Override
    public Optional<WorkspaceProjectCountDTO> findById(WorkspaceId workspaceId) {
        return Optional.ofNullable(workspaceProjectCountDTOs.get(workspaceId));
    }

}
