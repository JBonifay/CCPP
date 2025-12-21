package fr.joffreybonifay.ccpp.authentication.infrastructure.rest;

import fr.joffreybonifay.ccpp.authentication.application.LoginHandler;
import fr.joffreybonifay.ccpp.authentication.application.SignupHandler;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.AuthTokens;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SignupHandler signupHandler;
    private final LoginHandler loginHandler;

    public AuthController(SignupHandler signupHandler, LoginHandler loginHandler) {
        this.signupHandler = signupHandler;
        this.loginHandler = loginHandler;
    }

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupRequest request) {
        signupHandler.handle(request);
    }

    @PostMapping("/login")
    public AuthTokens login(@Valid @RequestBody LoginRequest request) {
        return loginHandler.handle(request);
    }

}
