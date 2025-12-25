package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserUniquenessChecker implements UserUniquenessChecker {

    private final UserReadRepository userReadRepository;

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return userReadRepository.findByEmail(email).isPresent();
    }
}
