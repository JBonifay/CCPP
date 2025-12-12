package io.joffrey.ccpp.projectplanning.application.command.handler;

public interface CommandHandler<Command> {
    void handle(Command command);
}
