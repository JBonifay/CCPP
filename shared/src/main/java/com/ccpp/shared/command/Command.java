package com.ccpp.shared.command;

import java.util.UUID;

public interface Command {
    UUID getCommandId();
    UUID getAggregateId();
    UUID getCorrelationId();
    UUID getCausationId();
}
