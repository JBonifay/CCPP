package fr.joffreybonifay.ccpp.usermanagement.application.query.repository;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;

import java.util.Optional;

public interface UserReadRepository {
    void save(UserDTO user);
    void update(UserDTO user);
    Optional<UserDTO> findById(UserId userId);
    Optional<UserDTO> findByEmail(String email);
}
