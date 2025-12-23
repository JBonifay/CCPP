package fr.joffreybonifay.ccpp.workspace.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;

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
