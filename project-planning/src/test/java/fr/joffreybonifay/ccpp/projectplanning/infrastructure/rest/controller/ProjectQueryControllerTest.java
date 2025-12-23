package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.controller;


import fr.joffreybonifay.ccpp.projectplanning.AbstractE2eTest;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.InvitationStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ProjectQueryControllerTest extends AbstractE2eTest {

    @Test
    void should_get_projects_list() {
        var workspaceId = UUID.randomUUID();
        aProjectListExists(workspaceId);

        given()
                .header("X-Workspace-Id", workspaceId)
                .header("X-User-Id", UUID.randomUUID())
                .when()
                .get("/projects")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", equalTo(1))
                .body("[0].projectId", equalTo(projectIdGenerator.getValue()))
                .body("[0].workspaceId", equalTo(workspaceId.toString()))
                .body("[0].title", equalTo("Project test"))
                .body("[0].status", equalTo("READY"))
                .body("[0].totalBudget", equalTo(10000))
                .body("[0].participantCount", equalTo(1));
    }

    @Test
    void should_enforce_workspace_isolation() {
        var workspace1 = new WorkspaceId(UUID.randomUUID());
        var workspace2 = new WorkspaceId(UUID.randomUUID());
        var userId = new UserId(UUID.randomUUID());

        projectListRepository.save(new ProjectListDTO(
                new ProjectId(UUID.randomUUID()),
                workspace1,
                "Workspace 1 Project",
                ProjectStatus.READY,
                BigDecimal.valueOf(10000),
                1
        ));
        projectListRepository.save(new ProjectListDTO(
                new ProjectId(UUID.randomUUID()),
                workspace2,
                "Workspace 2 Project",
                ProjectStatus.READY,
                BigDecimal.valueOf(10000),
                1
        ));

        given()
                .header("X-Workspace-Id", workspace1.value().toString())
                .header("X-User-Id", userId.value().toString())
                .when()
                .get("/projects")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].title", equalTo("Workspace 1 Project"));

        given()
                .header("X-Workspace-Id", workspace2.value().toString())
                .header("X-User-Id", userId.value().toString())
                .when()
                .get("/projects")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].title", equalTo("Workspace 2 Project"));
    }


    @Test
    void should_get_project_detail() {
        var workspaceId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        projectDetailRepository.save(new ProjectDetailDTO(
                projectIdGenerator.generate(),
                new WorkspaceId(workspaceId),
                "Project test",
                "Project description",
                ProjectStatus.READY,
                List.of(new BudgetItemDTO(new BudgetItemId(UUID.fromString("678fae98-70e0-4fdd-845f-d12753a76aa8")), "A budget item", new Money(BigDecimal.valueOf(10), Currency.getInstance("EUR")))),
                List.of(new ParticipantDTO(new ParticipantId(UUID.fromString("67dfc6b2-2a7f-4f47-99f1-97f214b5ff97")), "McFly", "mcfly@mcfly.com", InvitationStatus.INVITED)),
                List.of(new NoteDTO("Example of a note", new UserId(userId))),
                new DateRange(LocalDate.of(2015, 1, 2), LocalDate.of(2015, 1, 31))
        ));

        given()
                .header("X-Workspace-Id", workspaceId)
                .header("X-User-Id", UUID.randomUUID())
                .when()
                .get("/projects/" + projectIdGenerator.getValue())
                .then()
                .log().all()
                .statusCode(200)
                .body("projectId", equalTo(projectIdGenerator.getValue()))
                .body("workspaceId", equalTo(workspaceId.toString()))
                .body("title", equalTo("Project test"))
                .body("description", equalTo("Project description"))
                .body("status", equalTo("READY"))
                .body("budgetItems.size()", equalTo(1))
                .body("budgetItems[0].id", equalTo("678fae98-70e0-4fdd-845f-d12753a76aa8"))
                .body("budgetItems[0].description", equalTo("A budget item"))
                .body("budgetItems[0].value", equalTo(10))
                .body("budgetItems[0].currency", equalTo("EUR"))
                .body("participants.size()", equalTo(1))
                .body("participants[0].participantId", equalTo("67dfc6b2-2a7f-4f47-99f1-97f214b5ff97"))
                .body("participants[0].name", equalTo("McFly"))
                .body("participants[0].email", equalTo("mcfly@mcfly.com"))
                .body("participants[0].status", equalTo("INVITED"))
                .body("notes.size()", equalTo(1))
                .body("notes[0].userId", equalTo(userId.toString()))
                .body("notes[0].content", equalTo("Example of a note"))
                .body("startDate", equalTo("2015-01-02"))
                .body("endDate", equalTo("2015-01-31"));
    }

    private void aProjectListExists(UUID workspaceId) {
        projectListRepository.save(new ProjectListDTO(
                projectIdGenerator.generate(),
                new WorkspaceId(workspaceId),
                "Project test",
                ProjectStatus.READY,
                BigDecimal.valueOf(10000),
                1
        ));
    }

}
