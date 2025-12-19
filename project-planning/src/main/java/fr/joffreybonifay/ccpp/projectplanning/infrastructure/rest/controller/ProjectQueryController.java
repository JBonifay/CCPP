package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.controller;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.query.QueryBus;
import fr.joffreybonifay.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import fr.joffreybonifay.ccpp.projectplanning.application.query.GetProjectListQuery;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto.ProjectDetailResponse;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto.ProjectListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectQueryController {

    private final QueryBus queryBus;

    public ProjectQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @GetMapping
    public ResponseEntity<List<ProjectListResponse>> getProjects() {
        List<ProjectListDTO> projects = queryBus.execute(new GetProjectListQuery(RequestContext.getWorkspaceId()));
        return ResponseEntity.ok(ProjectListResponse.from(projects));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetails(@PathVariable UUID projectId) {
        ProjectDetailDTO projectDetailDTO = queryBus.execute(new GetProjectDetailQuery(new ProjectId(projectId), RequestContext.getWorkspaceId()));
        return ResponseEntity.ok(ProjectDetailResponse.from(projectDetailDTO));
    }

}
