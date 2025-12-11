package io.joffrey.ccpp.projectplanning.infrastructure.event;

import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.application.query.handler.ProjectEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SpringEventBridge {

    private final ProjectEventHandler handler;

    public SpringEventBridge(ProjectEventHandler handler) {
        this.handler = handler;
    }

    @EventListener
    public void on(ProjectCreated event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ProjectDetailsUpdated event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ProjectMarkedAsReady event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ProjectTimelineChanged event) {
        handler.handle(event);
    }

    @EventListener
    public void on(BudgetItemAdded event) {
        handler.handle(event);
    }

    @EventListener
    public void on(BudgetItemUpdated event) {
        handler.handle(event);
    }

    @EventListener
    public void on(BudgetItemRemoved event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ProjectBudgetCapExceeded event) {
        handler.handle(event);
    }

    @EventListener
    public void on(NoteAdded event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ParticipantInvited event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ParticipantAcceptedInvitation event) {
        handler.handle(event);
    }

    @EventListener
    public void on(ParticipantDeclinedInvitation event) {
        handler.handle(event);
    }

}
