package fr.joffreybonifay.ccpp.authentication.infrastructure.repository;

import fr.joffreybonifay.ccpp.authentication.domain.Account;
import fr.joffreybonifay.ccpp.authentication.domain.repository.AccountRepository;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountRepositoryAdapter implements AccountRepository {

    private final SpringAccountRepository jpa;

    public AccountRepositoryAdapter(SpringAccountRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Account account) {
        var entity = new AccountEntity();
        entity.setUserId(account.userId());
        entity.setEmail(account.email().value());
        entity.setDisplayName(account.displayName());
        entity.setWorkspaceId(account.workspaceId().value());
        entity.setPasswordHash(account.passwordHash());
        jpa.save(entity);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpa.findByEmail(email)
                .map(e -> new Account(e.getUserId(), new Email(e.getEmail()), new WorkspaceId(e.getWorkspaceId()), e.getDisplayName(), e.getPasswordHash()));
    }

}
