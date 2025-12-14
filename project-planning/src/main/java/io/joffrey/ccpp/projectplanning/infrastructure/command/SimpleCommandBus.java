package io.joffrey.ccpp.projectplanning.infrastructure.command;

import io.joffrey.ccpp.projectplanning.application.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.handler.CommandHandler;

import java.util.Map;

public class SimpleCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?>> commandHandlers;

    public SimpleCommandBus(Map<Class<?>, CommandHandler<?>> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> void execute(C command) {
        var handler = commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for: " + command.getClass().getSimpleName()
            );
        }
        ((CommandHandler<C>) handler).handle(command);

    }
}


