package com.ccpp.shared.command;

import java.util.Map;

public class SimpleCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler> commandHandlers;

    public SimpleCommandBus(Map<Class<?>, CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Override
    public void execute( Command command) {
        var handler = commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for: " + command.getClass().getSimpleName()
            );
        }
        handler.handle(command);

    }
}


