package com.ccpp.shared.command;

public interface CommandHandler<C extends Command> {
    void handle(C command);
}
