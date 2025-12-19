package fr.joffreybonifay.ccpp.shared.eventbus;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {

    private final List<Consumer<DomainEvent>> subscribers = new ArrayList<>();

    public void subscribe(Consumer<DomainEvent> handler) {
        subscribers.add(handler);
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(event -> subscribers.forEach(handler -> handler.accept(event)));
    }

}
