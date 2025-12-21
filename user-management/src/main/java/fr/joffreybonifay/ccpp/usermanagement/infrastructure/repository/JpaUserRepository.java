package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.Role;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserStatus;
import fr.joffreybonifay.ccpp.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaUserRepository implements UserRepository {

    private final SpringUserJpaRepository jpa;

    public JpaUserRepository(SpringUserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        jpa.save(toEntity(user));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsById(UserId userId) {
        return jpa.existsById(userId.value());
    }

    private UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setId(user.getId().value());
        e.setEmail(user.getEmail().value());
        e.setDisplayName(user.getDisplayName());
        e.setStatus(UserStatusEntity.valueOf(user.getStatus().name()));
        e.setRoles(
                user.getRoles().stream()
                        .map(r -> RoleEntity.valueOf(r.name()))
                        .collect(Collectors.toSet())
        );
        return e;
    }

    private User toDomain(UserEntity e) {
        return new User(
                new UserId(e.getId()),
                new Email(e.getEmail()),
                e.getDisplayName(),
                UserStatus.valueOf(e.getStatus().name()),
                e.getRoles().stream()
                        .map(r -> Role.valueOf(r.name()))
                        .collect(Collectors.toSet())
        );
    }
}
