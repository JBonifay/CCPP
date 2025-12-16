package io.joffrey.ccpp.projectplanning.application.query.repository;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;

import java.util.List;
import java.util.Optional;

public interface ProjectListReadRepository {
    void save(ProjectListDTO dto);
    void update(ProjectListDTO dto);

    Optional<ProjectListDTO> findById(ProjectId projectId);
    List<ProjectListDTO> findByWorkspaceId(WorkspaceId workspaceId);
}
