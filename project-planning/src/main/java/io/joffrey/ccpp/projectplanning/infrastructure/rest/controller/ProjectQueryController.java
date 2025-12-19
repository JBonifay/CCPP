package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.query.QueryBus;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.ProjectDetailResponse;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.ProjectListResponse;
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
