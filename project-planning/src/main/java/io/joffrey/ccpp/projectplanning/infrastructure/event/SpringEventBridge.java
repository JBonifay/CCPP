package io.joffrey.ccpp.projectplanning.infrastructure.event;

import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.query.handler.ProjectEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component  // ← Spring annotation ONLY in infrastructure!
public class SpringEventBridge {

    private final ProjectEventHandler handler;

    public SpringEventBridge(ProjectEventHandler handler) {
        this.handler = handler;
    }

    @EventListener  // ← Spring annotation
    public void on(ProjectCreated event) {
        handler.handle(event);
    }

    @EventListener
    public void on(BudgetItemAdded event) {
        handler.handle(event);
    }
}
