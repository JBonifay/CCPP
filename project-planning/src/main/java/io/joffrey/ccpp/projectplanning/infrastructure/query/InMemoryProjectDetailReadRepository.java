package io.joffrey.ccpp.projectplanning.infrastructure.query;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProjectDetailReadRepository implements ProjectDetailReadRepository {

    private final Map<ProjectId, ProjectDetailDTO> store = new ConcurrentHashMap<>();

    @Override
    public void save(ProjectDetailDTO dto) {
        store.put(dto.projectId(), dto);
    }

    @Override
    public void update(ProjectDetailDTO dto) {
        store.put(dto.projectId(), dto);
    }

    @Override
    public Optional<ProjectDetailDTO> findById(ProjectId projectId) {
        return Optional.ofNullable(store.get(projectId));
    }

    @Override
    public List<ProjectDetailDTO> findByWorkspaceId(WorkspaceId workspaceId) {
        return store.values().stream()
                .filter(dto -> dto.workspaceId().equals(workspaceId))
                .toList();
    }

    @Override
    public void deleteById(ProjectId projectId) {
        store.remove(projectId);
    }
}
