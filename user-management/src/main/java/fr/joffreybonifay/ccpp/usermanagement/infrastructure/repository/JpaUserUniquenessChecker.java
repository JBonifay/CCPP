package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserUniquenessChecker implements UserUniquenessChecker {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return jpaUserRepository.findByEmail(email).isPresent();
    }

}
