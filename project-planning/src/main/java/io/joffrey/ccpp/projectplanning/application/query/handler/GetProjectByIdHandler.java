package io.joffrey.ccpp.projectplanning.application.query.handler;

import io.joffrey.ccpp.projectplanning.application.query.GetProjectByIdQuery;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectReadModel;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectQueryRepository;

import java.util.Optional;

public class GetProjectByIdHandler {

    private final ProjectQueryRepository repository;

    public GetProjectByIdHandler(ProjectQueryRepository repository) {
        this.repository = repository;
    }

    public Optional<ProjectReadModel> handle(GetProjectByIdQuery query) {
        return repository.findById(query.projectId().value());
    }
}
