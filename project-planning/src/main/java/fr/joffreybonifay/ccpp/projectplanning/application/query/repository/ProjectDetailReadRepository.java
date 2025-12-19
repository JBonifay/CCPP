package fr.joffreybonifay.ccpp.projectplanning.application.query.repository;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;

import java.util.Optional;

public interface ProjectDetailReadRepository {
    void save(ProjectDetailDTO dto);
    void update(ProjectDetailDTO dto);

    Optional<ProjectDetailDTO> findById(ProjectId projectId);
}
