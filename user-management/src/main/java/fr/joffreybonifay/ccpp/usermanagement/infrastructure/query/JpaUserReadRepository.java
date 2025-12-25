package fr.joffreybonifay.ccpp.usermanagement.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserWorkspacesRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserJpaEntity;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserWorkspacesJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaUserReadRepository implements UserReadRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaUserWorkspacesRepository jpaUserWorkspacesRepository;

    public JpaUserReadRepository(
            JpaUserRepository jpaUserRepository,
            JpaUserWorkspacesRepository jpaUserWorkspacesRepository
    ) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaUserWorkspacesRepository = jpaUserWorkspacesRepository;
    }

    @Override
    public void save(UserDTO user) {
        jpaUserRepository.save(new UserJpaEntity(
                user.userId().value(),
                user.email(),
                user.passwordHash(),
                user.fullName()
        ));

        for (UUID workspaceId : user.workspaceIds()) {
            jpaUserWorkspacesRepository.save(new UserWorkspacesJpaEntity(
                    user.userId().value(),
                    workspaceId
            ));
        }
    }

    @Override
    public void update(UserDTO user) {
        save(user);
    }

    @Override
    public Optional<UserDTO> findById(UserId userId) {
        return jpaUserRepository.findById(userId.value())
                .map(this::toDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDTO);
    }

    private UserDTO toDTO(UserJpaEntity entity) {
        Set<UUID> workspaceIds = jpaUserWorkspacesRepository
                .findByUserId(entity.getId())
                .stream()
                .map(UserWorkspacesJpaEntity::getWorkspaceId)
                .collect(Collectors.toSet());

        return new UserDTO(
                new UserId(entity.getId()),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFullName(),
                workspaceIds
        );
    }
}
