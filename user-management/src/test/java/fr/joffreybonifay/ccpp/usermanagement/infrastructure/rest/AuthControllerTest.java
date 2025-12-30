package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.AbstractE2eTest;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class AuthControllerTest extends AbstractE2eTest {

    @Test
    void should_login() {
        userReadRepository.save(new UserDTO(
                new UserId(UUID.randomUUID()),
                "joffreybonifay83@gmail.com",
                "$2a$10$cVOeEZG28y6XGaDGVc2IIeYE4TEUyrWZGCA5hVYqQ1o5CxXc45skm",
                "Joffrey",
                Collections.emptySet()));

        given()
                .header("X-Workspace-Id", UUID.randomUUID().toString())
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(ContentType.JSON)
                .body(new LoginRequest(
                        "joffreybonifay83@gmail.com",
                        "password"
                ))
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }

}
