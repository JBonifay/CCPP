package io.joffrey.ccpp.projectplanning.application.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectCreationResponseEventHandler implements EventHandler {

    @Override
    public void handle(DomainEvent event) {

    }

}
