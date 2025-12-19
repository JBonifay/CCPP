package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.controller;

import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto.*;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.NoteDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ParticipantDTO;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.InvitationStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.projectplanning.AbstractE2eTest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.Matchers.equalTo;

class ProjectCommandControllerTest extends AbstractE2eTest {

    @Test
    void should_create_project() {
        given()
                .header("X-Workspace-Id", UUID.randomUUID().toString())
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(ContentType.JSON)
                .body(new RequestProjectCreationRequest(
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
                .statusCode(HttpStatus.CREATED.value())
                .body("projectId", equalTo(projectIdGenerator.getValue()));
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
                .statusCode(HttpStatus.CREATED.value());

        assertThat(projectDetailRepository.findById(projectId).get().notes()).containsExactly(
                new NoteDTO(
                        "Remember to book equipment",
                        userId
                ));
    }

    @Test
    void should_mark_project_as_ready() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .when()
                .post("/api/projects/" + projectId.value() + "/ready")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(projectDetailRepository.findById(projectId).get().status())
                .isEqualTo(ProjectStatus.READY);
    }

    @Test
    void should_change_project_timeline() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .contentType(ContentType.JSON)
                .body(new UpdateProjectTimelineRequest(
                        LocalDate.of(2025, 1, 2),
                        LocalDate.of(2025, 1, 25)
                ))
                .when()
                .patch("/api/projects/" + projectId.value() + "/timeline")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(projectDetailRepository.findById(projectId).get().timeline()).isEqualTo(
                new DateRange(LocalDate.of(2025, 1, 2), LocalDate.of(2025, 1, 25)));
    }

    @Test
    void should_update_project_details() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());
        var projectId = new ProjectId(UUID.randomUUID());
        aProjectExist(workspaceId, userId, projectId);

        given()
                .header("X-Workspace-Id", workspaceId.value().toString())
                .header("X-User-Id", userId.value().toString())
                .contentType(ContentType.JSON)
                .body(new UpdateProjectDetailsRequest(
                        "New title",
                        "New description"
                ))
                .when()
                .patch("/api/projects/" + projectId.value() + "/details")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(projectDetailRepository.findById(projectId).get().title()).isEqualTo("New title");
        assertThat(projectDetailRepository.findById(projectId).get().description()).isEqualTo("New description");
    }

    @Nested
    class BudgetItemTests {
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
        void should_remove_budget_item() {
            var workspaceId = new WorkspaceId(UUID.randomUUID());
            var userId = new UserId(UUID.randomUUID());
            var projectId = new ProjectId(UUID.randomUUID());
            var budgetItemId = new BudgetItemId(UUID.randomUUID());
            aProjectExist(workspaceId, userId, projectId);
            aBudgetItemIsPresent(projectId, budgetItemId);

            given()
                    .header("X-Workspace-Id", workspaceId.value().toString())
                    .header("X-User-Id", userId.value().toString())
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/api/projects/" + projectId.value() + "/budget-items/" + budgetItemId.value())
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(projectDetailRepository.findById(projectId).get().budgetItems()).isEmpty();
        }

        @Test
        void should_update_budget_item() {
            var workspaceId = new WorkspaceId(UUID.randomUUID());
            var userId = new UserId(UUID.randomUUID());
            var projectId = new ProjectId(UUID.randomUUID());
            var budgetItemId = new BudgetItemId(UUID.randomUUID());
            aProjectExist(workspaceId, userId, projectId);
            aBudgetItemIsPresent(projectId, budgetItemId);

            given()
                    .header("X-Workspace-Id", workspaceId.value().toString())
                    .header("X-User-Id", userId.value().toString())
                    .contentType(ContentType.JSON)
                    .body(new UpdateBudgetItemRequest(
                            "Hotel 5x nights",
                            new Money(new BigDecimal("9999"), Currency.getInstance("USD"))
                    ))
                    .when()
                    .patch("/api/projects/" + projectId.value() + "/budget-items/" + budgetItemId.value())
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(projectDetailRepository.findById(projectId).get().budgetItems()).containsExactly(
                    new BudgetItemDTO(
                            budgetItemId,
                            "Hotel 5x nights",
                            new Money(new BigDecimal("9999"), Currency.getInstance("USD"))
                    ));
        }

    }

    @Nested
    class ParticipantTests {

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
                            InvitationStatus.INVITED
                    ));
        }

        @Test
        void should_accept_participant_invitation() {
            var workspaceId = new WorkspaceId(UUID.randomUUID());
            var userId = new UserId(UUID.randomUUID());
            var projectId = new ProjectId(UUID.randomUUID());
            var participantId = new ParticipantId(UUID.randomUUID());
            aProjectExist(workspaceId, userId, projectId);
            aParticipantIsInvited(projectId, participantId);

            given()
                    .header("X-Workspace-Id", workspaceId.value().toString())
                    .header("X-User-Id", userId.value().toString())
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/api/projects/" + projectId.value() + "/participants/" + participantId.value() + "/accept")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(projectDetailRepository.findById(projectId).get().participants()).containsExactly(
                    new ParticipantDTO(
                            participantId,
                            "McFly",
                            "mcfly@mcfly.com",
                            InvitationStatus.ACCEPTED
                    ));
        }

        @Test
        void should_decline_participant_invitation() {
            var workspaceId = new WorkspaceId(UUID.randomUUID());
            var userId = new UserId(UUID.randomUUID());
            var projectId = new ProjectId(UUID.randomUUID());
            var participantId = new ParticipantId(UUID.randomUUID());
            aProjectExist(workspaceId, userId, projectId);
            aParticipantIsInvited(projectId, participantId);

            given()
                    .header("X-Workspace-Id", workspaceId.value().toString())
                    .header("X-User-Id", userId.value().toString())
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/api/projects/" + projectId.value() + "/participants/" + participantId.value() + "/decline")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(projectDetailRepository.findById(projectId).get().participants()).containsExactly(
                    new ParticipantDTO(
                            participantId,
                            "McFly",
                            "mcfly@mcfly.com",
                            InvitationStatus.DECLINED
                    ));

        }
    }

}
