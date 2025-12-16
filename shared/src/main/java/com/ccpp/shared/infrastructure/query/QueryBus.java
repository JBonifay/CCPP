package com.ccpp.shared.infrastructure.query;

public interface QueryBus {
    <Q, R> R execute(Q query);
}
