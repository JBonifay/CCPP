package io.joffrey.ccpp.projectplanning.application.query.repository;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;

import java.util.List;
import java.util.Optional;

public interface ProjectDetailReadRepository {
    void save(ProjectDetailDTO dto);
    void update(ProjectDetailDTO dto);
    void deleteById(ProjectId projectId);

    Optional<ProjectDetailDTO> findById(ProjectId projectId);
    List<ProjectDetailDTO> findByWorkspaceId(WorkspaceId workspaceId);
}
