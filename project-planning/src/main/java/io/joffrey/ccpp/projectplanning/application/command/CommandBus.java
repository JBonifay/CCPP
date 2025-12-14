package io.joffrey.ccpp.projectplanning.application.command;

public interface CommandBus {
    <C> void execute(C command);
}
