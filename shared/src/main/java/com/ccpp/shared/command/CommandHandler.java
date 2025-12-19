package com.ccpp.shared.command;

@FunctionalInterface
public interface CommandHandler<C extends Command> {
    void handle(C command);
}
