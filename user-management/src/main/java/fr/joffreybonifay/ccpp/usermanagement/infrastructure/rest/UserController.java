package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest;

import fr.joffreybonifay.ccpp.usermanagement.application.command.UpdateMyProfileCommand;
import fr.joffreybonifay.ccpp.usermanagement.application.command.UpdateMyProfileHandler;
import fr.joffreybonifay.ccpp.usermanagement.application.query.GetMyProfileHandler;
import fr.joffreybonifay.ccpp.usermanagement.application.query.GetMyProfileQuery;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.UpdateMyProfileRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final GetMyProfileHandler getMyProfile;
    private final UpdateMyProfileHandler updateMyProfile;

    public UserController(
            GetMyProfileHandler getMyProfile,
            UpdateMyProfileHandler updateMyProfile
    ) {
        this.getMyProfile = getMyProfile;
        this.updateMyProfile = updateMyProfile;
    }

    @GetMapping("/me")
    public User me(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return getMyProfile.handle(new GetMyProfileQuery(userId));
    }

    @PatchMapping("/me")
    public void update(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UpdateMyProfileRequest body
    ) {
        updateMyProfile.handle(new UpdateMyProfileCommand(
                userId.toString(),
                body.displayName()
        ));
    }
}
