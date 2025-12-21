package fr.joffreybonifay.ccpp.authentication.domain.repository;

import fr.joffreybonifay.ccpp.authentication.domain.Account;

import java.util.Optional;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findByEmail(String email);
}
