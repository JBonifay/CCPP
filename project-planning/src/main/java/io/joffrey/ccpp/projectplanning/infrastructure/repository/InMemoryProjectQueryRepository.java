package io.joffrey.ccpp.projectplanning.infrastructure.repository;

import io.joffrey.ccpp.projectplanning.query.model.ProjectReadModel;
import io.joffrey.ccpp.projectplanning.query.repository.ProjectQueryRepository;

import java.util.*;

public class InMemoryProjectQueryRepository implements ProjectQueryRepository {

    private final Map<UUID, ProjectReadModel> projects = new HashMap<>();

    @Override
    public List<ProjectReadModel> findByWorkspaceId(UUID workspaceId) {
        return projects.values().stream().filter(p -> p.workspaceId().equals(workspaceId)).toList();
    }

    @Override
    public Optional<ProjectReadModel> findById(UUID projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

    @Override
    public ProjectReadModel save(ProjectReadModel model) {
        return projects.put(model.projectId(), model);
    }

}
