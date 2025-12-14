package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;


import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.AbstractE2eTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ProjectQueryControllerTest extends AbstractE2eTest {

    @Test
    void should_get_projects_list() {
        var workspaceId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        projectListRepository.save(new ProjectListDTO(
                projectIdGenerator.generate().value().toString(),
                new WorkspaceId(workspaceId).value().toString(),
                "Project test",
                "READY",
                BigDecimal.valueOf(10000),
                1
        ));

        given()
                .header("X-Workspace-Id", workspaceId)
                .header("X-User-Id", userId)
                .when()
                .get("/api/projects")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].projectId.value", equalTo(projectIdGenerator.getValue()))
                .body("[0].workspaceId.value", equalTo(workspaceId.toString()))
                .body("[0].title", equalTo("Project test"))
                .body("[0].status", equalTo("READY"))
                .body("[0].totalBudget", equalTo(10000))
                .body("[0].participantCount", equalTo(1));
    }

    @Test
    void should_get_project_detail() {
        // GIVEN - project already created
        // WHEN - GET /api/projects/{projectId}
        // THEN - returns complete project details
    }

    @Test
    void should_enforce_workspace_isolation() {
        // GIVEN - 2 projects in different workspaces
        // WHEN - query with workspace1 headers
        // THEN - only sees workspace1 project
    }

}
