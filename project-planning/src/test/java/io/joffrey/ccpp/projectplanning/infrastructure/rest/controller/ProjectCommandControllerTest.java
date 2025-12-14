package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import io.joffrey.ccpp.projectplanning.infrastructure.rest.AbstractE2eTest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.CreateProjectRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
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

}
