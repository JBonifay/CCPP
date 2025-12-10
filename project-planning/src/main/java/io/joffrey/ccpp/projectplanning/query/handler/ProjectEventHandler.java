package io.joffrey.ccpp.projectplanning.query.handler;

import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.query.model.ProjectReadModel;
import io.joffrey.ccpp.projectplanning.query.repository.ProjectQueryRepository;

import java.math.BigDecimal;

public class ProjectEventHandler {

    private final ProjectQueryRepository repository;

    public ProjectEventHandler(ProjectQueryRepository repository) {
        this.repository = repository;
    }

    public void handle(ProjectCreated event) {
        var readModel = new ProjectReadModel(
                event.projectId().value(),
                event.workspaceId().value(),
                event.title(),
                event.description(),
                event.timeline().startDate(),
                event.timeline().endDate(),
                "PLANNING",
                BigDecimal.ZERO,
                0,
                0
        );
        repository.save(readModel);
    }

    public void handle(BudgetItemAdded event) {
        // Update budget
    }

}
