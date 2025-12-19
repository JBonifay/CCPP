package fr.joffreybonifay.ccpp.projectplanning.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;

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

}
