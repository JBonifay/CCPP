package com.ccpp.shared.infrastructure.command;

public interface CommandBus {
    <C extends Command> void register(Class<C> commandType, CommandHandler<C> handler);
    <C extends Command> void execute(C command);
}
