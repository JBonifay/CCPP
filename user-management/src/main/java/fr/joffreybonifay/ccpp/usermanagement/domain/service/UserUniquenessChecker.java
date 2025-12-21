package fr.joffreybonifay.ccpp.usermanagement.domain.service;

public interface UserUniquenessChecker {
    boolean isEmailAlreadyInUse(String email);
}
