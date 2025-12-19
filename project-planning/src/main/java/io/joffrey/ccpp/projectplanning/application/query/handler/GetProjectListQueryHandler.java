package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.query.QueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;

import java.util.List;

public class GetProjectListQueryHandler implements QueryHandler<GetProjectListQuery, List<ProjectListDTO>> {

    private final ProjectListReadRepository repository;

    public GetProjectListQueryHandler(ProjectListReadRepository repository) {
        this.repository = repository;
    }

    public List<ProjectListDTO> handle(GetProjectListQuery query) {
        return repository.findByWorkspaceId(query.workspaceId());
    }

}
