package io.joffrey.ccpp.projectplanning.application.query.repository;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;

import java.util.Optional;

public interface ProjectDetailReadRepository {
    void save(ProjectDetailDTO dto);
    void update(ProjectDetailDTO dto);

    Optional<ProjectDetailDTO> findById(ProjectId projectId);
}
