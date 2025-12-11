package io.joffrey.ccpp.projectplanning.application.query.handler;

import io.joffrey.ccpp.projectplanning.application.query.GetProjectsByWorkspaceQuery;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectReadModel;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectQueryRepository;

import java.util.List;

public class GetProjectsByWorkspaceHandler {

    private final ProjectQueryRepository repository;

    public GetProjectsByWorkspaceHandler(ProjectQueryRepository repository) {
        this.repository = repository;
    }

    public List<ProjectReadModel> handle(GetProjectsByWorkspaceQuery query) {
        return repository.findByWorkspaceId(query.workspaceId().value());
    }
}
