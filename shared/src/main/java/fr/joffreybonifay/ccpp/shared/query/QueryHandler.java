package fr.joffreybonifay.ccpp.shared.query;

public interface QueryHandler<Query, Result> {
    Result handle(Query query);
}
