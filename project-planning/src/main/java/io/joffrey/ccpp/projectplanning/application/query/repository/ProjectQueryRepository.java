package io.joffrey.ccpp.projectplanning.application.query.repository;

import io.joffrey.ccpp.projectplanning.application.query.model.ProjectReadModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectQueryRepository {
      List<ProjectReadModel> findByWorkspaceId(UUID workspaceId);
      Optional<ProjectReadModel> findById(UUID projectId);
      ProjectReadModel save(ProjectReadModel model);
  }
