package fr.joffreybonifay.ccpp.usermanagement.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.WorkspaceDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserWorkspacesRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserJpaEntity;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserWorkspacesJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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

        for (WorkspaceDTO workspace : user.workspaces()) {
            jpaUserWorkspacesRepository.save(new UserWorkspacesJpaEntity(
                    user.userId().value(),
                    workspace.workspaceId().value(),
                    workspace.workspaceName(),
                    workspace.workspaceLogoUrl()
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
        return new UserDTO(
                new UserId(entity.getId()),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFullName(),
                jpaUserWorkspacesRepository
                        .findByUserId(entity.getId())
                        .stream()
                        .map(userWorkspacesJpaEntity ->
                                new WorkspaceDTO(
                                        new WorkspaceId(userWorkspacesJpaEntity.getWorkspaceId()),
                                        userWorkspacesJpaEntity.getWorkspaceName(),
                                        userWorkspacesJpaEntity.getWorkspaceName()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
