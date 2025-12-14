package io.joffrey.ccpp.projectplanning.application.query;

public interface QueryBus {
    <Q, R> R execute(Q query);
}
