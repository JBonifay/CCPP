package fr.joffreybonifay.ccpp.usermanagement.application.command;

public record UpdateMyProfileCommand(
        String userId,
        String displayName
) {}
