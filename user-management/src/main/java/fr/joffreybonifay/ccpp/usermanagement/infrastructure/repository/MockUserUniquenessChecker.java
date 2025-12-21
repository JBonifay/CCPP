package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;

import java.util.ArrayList;
import java.util.List;

public class MockUserUniquenessChecker implements UserUniquenessChecker {

    private final List<String> emails = new ArrayList<>();

    public void addEmail(String email) {
        this.emails.add(email);
    }

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return emails.contains(email);
    }

}
