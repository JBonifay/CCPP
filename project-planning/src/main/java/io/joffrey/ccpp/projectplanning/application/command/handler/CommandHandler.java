package io.joffrey.ccpp.projectplanning.application.command.handler;

public interface CommandHandler<TCommand> {
    void handle(TCommand command);
}
