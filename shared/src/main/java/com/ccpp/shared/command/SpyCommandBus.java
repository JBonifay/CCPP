package com.ccpp.shared.command;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpyCommandBus implements CommandBus {

    private final List<Command> executedCommands = new ArrayList<>();

    @Override
    public void execute(Command command) {
        executedCommands.add(command);
    }

    public void clear() {
        executedCommands.clear();
    }
}
