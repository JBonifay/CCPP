package fr.joffreybonifay.ccpp.usermanagement.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.AbstractE2eTest;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.WorkspaceDTO;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserWorkspacesRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserJpaEntity;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserWorkspacesJpaEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaUserReadRepositoryTest extends AbstractE2eTest {

    @Autowired
    JpaUserWorkspacesRepository jpaUserWorkspacesRepository;

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Autowired
    JpaUserReadRepository jpaUserReadRepository;

    @Test
    void should_save() {
        UserDTO userDTO = aUserIsPresent();

        assertThat(jpaUserRepository.findById(userDTO.userId().value()).get()).isEqualTo(
                new UserJpaEntity(
                        userDTO.userId().value(),
                        userDTO.email(),
                        userDTO.passwordHash(),
                        userDTO.fullName()
                )
        );
        assertThat(jpaUserWorkspacesRepository.findByUserId(userDTO.userId().value())).isEmpty();
    }

    @Test
    void should_update() {
        UserDTO userDTO = aUserIsPresent();

        WorkspaceDTO workspaceDTO = new WorkspaceDTO(new WorkspaceId(UUID.randomUUID()), "workspaceName", "workspaceLogoUrl");
        userDTO = new UserDTO(
                userDTO.userId(),
                userDTO.email(),
                userDTO.passwordHash(),
                userDTO.fullName(),
                List.of(workspaceDTO)
        );
        jpaUserReadRepository.update(userDTO);

        assertThat(jpaUserRepository.findById(userDTO.userId().value()).get()).isEqualTo(
                new UserJpaEntity(
                        userDTO.userId().value(),
                        userDTO.email(),
                        userDTO.passwordHash(),
                        userDTO.fullName()
                )
        );
        assertThat(jpaUserWorkspacesRepository.findByUserId(userDTO.userId().value())).isEqualTo(List.of(
                new UserWorkspacesJpaEntity(
                        userDTO.userId().value(),
                        workspaceDTO.workspaceId().value(),
                        workspaceDTO.workspaceName(),
                        workspaceDTO.workspaceLogoUrl()
                )
        ));
    }

    @Test
    void should_not_duplicate_workspace_on_multiple_updates() {
        UserDTO userDTO = aUserIsPresent();

        WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

        WorkspaceDTO workspaceDTO = new WorkspaceDTO(
                workspaceId,
                "workspaceName",
                "workspaceLogoUrl"
        );

        UserDTO updated = new UserDTO(
                userDTO.userId(),
                userDTO.email(),
                userDTO.passwordHash(),
                userDTO.fullName(),
                List.of(workspaceDTO)
        );

        // first update
        jpaUserReadRepository.update(updated);

        // second update with same workspace
        jpaUserReadRepository.update(updated);

        List<UserWorkspacesJpaEntity> workspaces =
                jpaUserWorkspacesRepository.findByUserId(userDTO.userId().value());

        assertThat(workspaces).hasSize(1);
        assertThat(workspaces.get(0)).isEqualTo(
                new UserWorkspacesJpaEntity(
                        userDTO.userId().value(),
                        workspaceId.value(),
                        "workspaceName",
                        "workspaceLogoUrl"
                )
        );
    }


    private @NonNull UserDTO aUserIsPresent() {
        UserDTO userDTO = new UserDTO(
                new UserId(UUID.randomUUID()),
                "email",
                "password_hash",
                "fullname",
                List.of());
        jpaUserReadRepository.save(userDTO);
        return userDTO;
    }

}
