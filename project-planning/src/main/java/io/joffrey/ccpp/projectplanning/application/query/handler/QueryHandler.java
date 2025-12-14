package io.joffrey.ccpp.projectplanning.application.query.handler;

public interface QueryHandler<Query, Result> {
    Result handle(Query query);
}
