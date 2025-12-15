package com.ccpp.shared.command;

public interface CommandBus {
    <C> void execute(C command);
}
