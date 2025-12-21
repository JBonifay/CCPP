package fr.joffreybonifay.ccpp.usermanagement.application.query;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GetMyProfileHandler {

    private final UserRepository repository;

    public GetMyProfileHandler(UserRepository repository) {
        this.repository = repository;
    }

    public User handle(GetMyProfileQuery query) {
        return repository.findById(new UserId(query.userId()))
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
