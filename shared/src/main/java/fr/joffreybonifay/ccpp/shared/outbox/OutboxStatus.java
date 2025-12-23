package fr.joffreybonifay.ccpp.shared.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED;

    public boolean needsProcessing() {
        return this == PENDING;
    }

    public boolean isFinal() {
        return this == PUBLISHED || this == FAILED;
    }

}
