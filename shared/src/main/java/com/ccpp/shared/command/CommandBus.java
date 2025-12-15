package com.ccpp.shared.command;

public interface CommandBus {
    void execute(Command command);
}
