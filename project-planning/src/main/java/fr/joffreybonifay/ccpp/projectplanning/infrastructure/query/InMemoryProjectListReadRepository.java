package fr.joffreybonifay.ccpp.projectplanning.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProjectListReadRepository implements ProjectListReadRepository {

    private final Map<ProjectId, ProjectListDTO> store = new ConcurrentHashMap<>();

    @Override
    public void save(ProjectListDTO dto) {
        store.put(dto.projectId(), dto);
    }

    @Override
    public void update(ProjectListDTO dto) {
        store.put(dto.projectId(), dto);
    }

    @Override
    public Optional<ProjectListDTO> findById(ProjectId projectId) {
        return Optional.ofNullable(store.get(projectId));
    }

    @Override
    public List<ProjectListDTO> findByWorkspaceId(WorkspaceId workspaceId) {
        return store.values().stream()
                .filter(dto -> dto.workspaceId().equals(workspaceId))
                .toList();
    }

}

