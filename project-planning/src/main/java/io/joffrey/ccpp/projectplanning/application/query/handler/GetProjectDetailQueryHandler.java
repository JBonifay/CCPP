package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.infrastructure.query.QueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.exception.ProjectNotFoundException;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;

public class GetProjectDetailQueryHandler implements QueryHandler<GetProjectDetailQuery, ProjectDetailDTO> {

    private final ProjectDetailReadRepository repository;

    public GetProjectDetailQueryHandler(ProjectDetailReadRepository repository) {
        this.repository = repository;
    }

    public ProjectDetailDTO handle(GetProjectDetailQuery query) {
        return repository.findById(query.projectId())
                .filter(dto -> dto.workspaceId().equals(query.workspaceId()))
                .orElseThrow(() -> new ProjectNotFoundException(query.projectId()));
    }

}
