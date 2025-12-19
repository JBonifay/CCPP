package fr.joffreybonifay.ccpp.projectplanning.integration;

import static org.assertj.core.api.Assertions.assertThat;

class CQRSIntegrationTest {
//
//    EventStore eventStore = new InMemoryEventStore();
//    ProjectListReadRepository listRepository = new InMemoryProjectListReadRepository();
//    ProjectDetailReadRepository detailRepository = new InMemoryProjectDetailReadRepository();
//
//    CreateProjectHandler createProjectHandler = new CreateProjectHandler(eventStore);
//    AddBudgetItemHandler addBudgetItemHandler = new AddBudgetItemHandler(eventStore);
//    UpdateBudgetItemHandler updateBudgetItemHandler = new UpdateBudgetItemHandler(eventStore);
//    RemoveBudgetItemHandler removeBudgetItemHandler = new RemoveBudgetItemHandler(eventStore);
//    InviteParticipantHandler inviteParticipantHandler = new InviteParticipantHandler(eventStore);
//    AddNoteHandler addNoteHandler = new AddNoteHandler(eventStore);
//    MarkProjectAsReadyHandler markReadyHandler = new MarkProjectAsReadyHandler(eventStore);
//
//    GetProjectListQueryHandler listQueryHandler = new GetProjectListQueryHandler(listRepository);
//    GetProjectDetailQueryHandler detailQueryHandler = new GetProjectDetailQueryHandler(detailRepository);
//
//    ProjectId projectId = new ProjectId(UUID.randomUUID());
//    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
//    UserId userId = new UserId(UUID.randomUUID());
//    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
//
//    UUID commandId = UUID.randomUUID();
//    UUID correlationId = UUID.randomUUID();
//
//    @Test
//    void should_project_created_project_to_both_read_models() {
//        createProjectHandler.handle(new CreateProjectCommand(
//                commandId,
//                workspaceId,
//                userId,
//                projectId,
//                "Q1 2025 Video Series",
//                "Educational content about Event Sourcing",
//                timeline,
//                BigDecimal.valueOf(5000),
//                correlationId
//        ));
//
//        assertThat(listQueryHandler.handle(new GetProjectListQuery(workspaceId))).containsExactly(
//                new ProjectListDTO(
//                        projectId,
//                        workspaceId,
//                        "Q1 2025 Video Series",
//                        ProjectStatus.PLANNING,
//                        BigDecimal.valueOf(5000),
//                        0
//                )
//        );
//
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//
//        assertThat(detail.title()).isEqualTo("Q1 2025 Video Series");
//        assertThat(detail.description()).isEqualTo("Educational content about Event Sourcing");
//        assertThat(detail.budgetItems()).isEmpty();
//    }
//
//    @Test
//    void should_update_projections_when_budget_items_added() {
//        // GIVEN - Project created
//        givenProjectCreated();
//
//        // WHEN - Add budget item
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var addCommand = new AddBudgetItemCommand(
//                commandId,
//                projectId,
//                budgetItemId,
//                "Hotel 2x nights",
//                Money.of(300, "USD"),
//                correlationId
//        );
//        addBudgetItemHandler.handle(addCommand);
//
//        // THEN - List projection shows updated total budget
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(300));
//
//        // AND - Detail projection shows budget item in collection
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.budgetItems())
//                .hasSize(1)
//                .first()
//                .satisfies(item -> {
//                    assertThat(item.description()).isEqualTo("Hotel 2x nights");
//                    assertThat(item.amount()).isEqualTo(Money.of(300, "USD"));
//                });
//    }
//
//    @Test
//    void should_update_projections_when_budget_items_updated() {
//        // GIVEN - Project with budget item
//        givenProjectCreated();
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        addBudgetItemHandler.handle(new AddBudgetItemCommand(
//                commandId,
//                projectId, budgetItemId, "Hotel", Money.of(300, "USD"),
//                correlationId
//        ));
//
//        // WHEN - Update budget item
//        var updateCommand = new UpdateBudgetItemCommand(
//                commandId,
//                projectId,
//                budgetItemId,
//                "Hotel (3 nights)",
//                Money.of(450, "USD"),
//                correlationId
//        );
//        updateBudgetItemHandler.handle(updateCommand);
//
//        // THEN - List projection shows updated total
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(450));
//
//        // AND - Detail projection shows updated item
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.budgetItems().get(0).description()).isEqualTo("Hotel (3 nights)");
//        assertThat(detail.budgetItems().get(0).amount()).isEqualTo(Money.of(450, "USD"));
//    }
//
//    @Test
//    void should_update_projections_when_budget_items_removed() {
//        // GIVEN - Project with budget item
//        givenProjectCreated();
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        addBudgetItemHandler.handle(new AddBudgetItemCommand(
//                commandId,
//                projectId, budgetItemId, "Hotel", Money.of(300, "USD"),
//                correlationId
//        ));
//
//        // WHEN - Remove budget item
//        var removeCommand = new RemoveBudgetItemCommand(commandId, projectId, budgetItemId, correlationId);
//        removeBudgetItemHandler.handle(removeCommand);
//
//        // THEN - List projection shows zero budget
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.ZERO);
//
//        // AND - Detail projection has no budget items
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.budgetItems()).isEmpty();
//    }
//
//    @Test
//    void should_update_projections_when_participants_invited() {
//        // GIVEN - Project created
//        givenProjectCreated();
//
//        // WHEN - Invite participant
//        var participantId = new ParticipantId(UUID.randomUUID());
//        var inviteCommand = new InviteParticipantCommand(
//                commandId,
//                projectId,
//                participantId,
//                "john@example.com",
//                "John Doe",
//                correlationId
//        );
//        inviteParticipantHandler.handle(inviteCommand);
//
//        // THEN - List projection shows participant count
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).participantCount()).isEqualTo(1);
//
//        // AND - Detail projection shows participant details
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.participants())
//                .hasSize(1)
//                .first()
//                .satisfies(p -> {
//                    assertThat(p.name()).isEqualTo("John Doe");
//                    assertThat(p.email()).isEqualTo("john@example.com");
//                    assertThat(p.invitationStatus()).isEqualTo(InvitationStatus.INVITED);
//                });
//    }
//
//    @Test
//    void should_update_projections_when_notes_added() {
//        // GIVEN - Project created
//        givenProjectCreated();
//
//        // WHEN - Add note
//        var addNoteCommand = new AddNoteCommand(
//                commandId,
//                projectId,
//                "Remember to book equipment",
//                userId,
//                correlationId
//        );
//        addNoteHandler.handle(addNoteCommand);
//
//        // THEN - Detail projection shows note
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.notes())
//                .hasSize(1)
//                .first()
//                .satisfies(note -> {
//                    assertThat(note.content()).isEqualTo("Remember to book equipment");
//                    assertThat(note.createdBy()).isEqualTo(userId);
//                });
//    }
//
//    @Test
//    void should_update_projections_when_marked_as_ready() {
//        // GIVEN - Project created
//        givenProjectCreated();
//
//        // WHEN - Mark as ready
//        var markReadyCommand = new MarkProjectAsReadyCommand(commandId, projectId, userId, correlationId);
//        markReadyHandler.handle(markReadyCommand);
//
//        // THEN - List projection shows READY status
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).status()).isEqualTo(ProjectStatus.READY);
//
//        // AND - Detail projection shows READY status
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.status()).isEqualTo(ProjectStatus.READY);
//    }
//
//    @Test
//    void should_enforce_multi_tenant_isolation_in_queries() {
//        // GIVEN - Projects in different workspaces
//        givenProjectCreated();
//
//        var otherWorkspace = new WorkspaceId(UUID.randomUUID());
//        var otherProjectId = new ProjectId(UUID.randomUUID());
//        createProjectHandler.handle(new CreateProjectCommand(
//                commandId,
//                otherWorkspace,
//                userId,
//                otherProjectId,
//                "Other Project",
//                "Description",
//                timeline,
//                BigDecimal.valueOf(1000),
//                correlationId
//        ));
//
//        // WHEN - Query for first workspace
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//
//        // THEN - Only returns projects from that workspace
//        assertThat(list)
//                .hasSize(1)
//                .extracting("title")
//                .containsExactly("Q1 2025 Video Series");
//    }
//
//    @Test
//    void should_handle_complex_scenario_with_multiple_operations() {
//        // GIVEN - Project created
//        givenProjectCreated();
//
//        // WHEN - Multiple operations
//        var budgetItem1 = new BudgetItemId(UUID.randomUUID());
//        var budgetItem2 = new BudgetItemId(UUID.randomUUID());
//
//        addBudgetItemHandler.handle(new AddBudgetItemCommand(commandId, projectId, budgetItem1, "Hotel", Money.of(300, "USD"), correlationId));
//        addBudgetItemHandler.handle(new AddBudgetItemCommand(commandId, projectId, budgetItem2, "Equipment", Money.of(500, "USD"), correlationId));
//
//        inviteParticipantHandler.handle(new InviteParticipantCommand(commandId, projectId, new ParticipantId(UUID.randomUUID()), "John Doe", "john@example.com", correlationId));
//        inviteParticipantHandler.handle(new InviteParticipantCommand(commandId, projectId, new ParticipantId(UUID.randomUUID()), "Jane Smith", "jane@example.com", correlationId));
//
//        addNoteHandler.handle(new AddNoteCommand(commandId, projectId, "Important reminder", userId, correlationId));
//
//        // THEN - List projection shows aggregated data
//        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
//        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(800));
//        assertThat(list.get(0).participantCount()).isEqualTo(2);
//
//        // AND - Detail projection shows all data
//        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
//        assertThat(detail.budgetItems()).hasSize(2);
//        assertThat(detail.participants()).hasSize(2);
//        assertThat(detail.notes()).hasSize(1);
//    }
//
//    private void givenProjectCreated() {
//        createProjectHandler.handle(new CreateProjectCommand(
//                commandId,
//                workspaceId,
//                userId,
//                projectId,
//                "Q1 2025 Video Series",
//                "Educational content about Event Sourcing",
//                timeline,
//                BigDecimal.valueOf(5000),
//                correlationId
//        ));
//    }
}
