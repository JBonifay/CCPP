package io.joffrey.ccpp.projectplanning.application.command.handler;

public interface CommandHandler<Comman> {
    void handle(Comman command);
}
