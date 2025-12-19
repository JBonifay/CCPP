package fr.joffreybonifay.ccpp.shared.query;

public interface QueryBus {
    <Q, R> R execute(Q query);
}
