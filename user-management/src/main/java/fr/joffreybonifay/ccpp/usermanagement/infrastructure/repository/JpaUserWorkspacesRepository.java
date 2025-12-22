package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserWorkspacesRepository extends JpaRepository<UserWorkspacesJpaEntity, UUID> {
}
