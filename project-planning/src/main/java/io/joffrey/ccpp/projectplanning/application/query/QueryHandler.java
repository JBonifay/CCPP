package io.joffrey.ccpp.projectplanning.application.query;

public interface QueryHandler<Query, Result> {
    Result handle(Query query);
}
