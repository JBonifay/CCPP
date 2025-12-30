package fr.joffreybonifay.ccpp.workspace.infrastructure.rest;

import fr.joffreybonifay.ccpp.workspace.AbstractE2eTest;
import fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto.CreateWorkspaceRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class WorkspaceCommandControllerTest extends AbstractE2eTest {

    @Test
    void should_create_project() {
        given()
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(ContentType.JSON)
                .body(new CreateWorkspaceRequest(
                        "Workspace name",
                        "logo url"
                ))
                .when()
                .post("/workspaces")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("workspaceId", equalTo(workspaceIdGenerator.getValue()));
    }

}
