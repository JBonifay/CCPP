package com.ccpp.shared.infrastructure.command;

@FunctionalInterface
public interface CommandHandler<C extends Command> {
    void handle(C command);
}
