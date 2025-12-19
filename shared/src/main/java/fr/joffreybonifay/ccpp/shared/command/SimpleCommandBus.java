package fr.joffreybonifay.ccpp.shared.command;

import java.util.HashMap;
import java.util.Map;

public class SimpleCommandBus implements CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<?>> commandHandlers = new HashMap<>();

    @Override
    public <C extends Command> void register(
            Class<C> commandType,
            CommandHandler<C> handler
    ) {
        commandHandlers.put(commandType, handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Command> void execute(C command) {
        var handler = (CommandHandler<C>) commandHandlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for: " + command.getClass().getSimpleName()
            );
        }

        handler.handle(command);
    }
}
