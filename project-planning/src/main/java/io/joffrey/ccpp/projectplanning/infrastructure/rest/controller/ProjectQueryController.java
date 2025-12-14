package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.QueryBus;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectQueryController {

    private final QueryBus queryBus;

    public ProjectQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @GetMapping
    public ResponseEntity<List<ProjectListDTO>> getProjects() {
        List<ProjectListDTO> projects = queryBus.execute(new GetProjectListQuery(RequestContext.getWorkspaceId()));
        return ResponseEntity.ok(projects);
    }

}
