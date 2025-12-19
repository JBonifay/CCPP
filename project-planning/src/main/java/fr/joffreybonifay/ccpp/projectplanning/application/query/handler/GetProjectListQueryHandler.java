package fr.joffreybonifay.ccpp.projectplanning.application.query.handler;

import fr.joffreybonifay.ccpp.shared.query.QueryHandler;
import fr.joffreybonifay.ccpp.projectplanning.application.query.GetProjectListQuery;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;

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
