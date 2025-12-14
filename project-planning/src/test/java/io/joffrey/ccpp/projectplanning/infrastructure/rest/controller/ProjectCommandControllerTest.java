package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.NoteDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ParticipantDTO;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.AbstractE2eTest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.AddBudgetItemRequest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.AddNoteRequest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.CreateProjectRequest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.InviteParticipantRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ProjectCommandControllerTest extends AbstractE2eTest {

    @Test
    void should_create_project_via_http() {
        given()
                .header("X-Workspace-Id", UUID.randomUUID().toString())
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(ContentType.JSON)
                .body(new CreateProjectRequest(
                        "Q1 2025 Video Series",
                        "Educational content",
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 3, 31),
                        BigDecimal.valueOf(5000)
                ))
                .when()
                .post("/api/projects")
                .then()
                .log().all()
                .statusCode(201)
                .body("projectId", equalTo(projectIdGenerator.getValue()));
    }

    @Test
    void should_add_budget_item() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .contentType(ContentType.JSON)
                .body(new AddBudgetItemRequest(
                        "Hotel 2x nights",
                        new BigDecimal("300"),
                        "USD"
                ))
                .when()
                .post("/api/projects/" + projectId.value() + "/budget-items")
                .then()
                .log().all()
                .statusCode(201);

        assertThat(projectDetailRepository.findById(projectId).get().budgetItems()).containsExactly(
                new BudgetItemDTO(
                        budgetItemIdGenerator.generate(),
                        "Hotel 2x nights",
                        new Money(new BigDecimal("300"), Currency.getInstance("USD"))
                ));
    }

    @Test
    void should_invite_participant() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .contentType(ContentType.JSON)
                .body(new InviteParticipantRequest(
                        "mcfly@mcfly.com",
                        "McFly"
                ))
                .when()
                .post("/api/projects/" + projectId.value() + "/participants")
                .then()
                .log().all()
                .statusCode(201);

        assertThat(projectDetailRepository.findById(projectId).get().participants()).containsExactly(
                new ParticipantDTO(
                        participantIdGenerator.generate(),
                        "McFly",
                        "mcfly@mcfly.com",
                        "INVITED"
                ));
    }

    @Test
    void should_add_note() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .contentType(ContentType.JSON)
                .body(new AddNoteRequest(
                        "Remember to book equipment"
                ))
                .when()
                .post("/api/projects/" + projectId.value() + "/notes")
                .then()
                .statusCode(201);

        assertThat(projectDetailRepository.findById(projectId).get().notes()).containsExactly(
                new NoteDTO(
                        "Remember to book equipment",
                        userId
                ));
    }

}
