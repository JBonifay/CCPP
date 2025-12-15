package com.ccpp.shared.command;

public interface CommandHandler<Command> {
    void handle(Command command);
}
