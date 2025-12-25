import { Component, inject, input, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { ProgressSpinner } from 'primeng/progressspinner';
import { Tag } from 'primeng/tag';
import { ProjectStore } from '../stores/project.store';

@Component({
  selector: 'app-project-detail',
  imports: [RouterLink, DecimalPipe, Button, Card, Tabs, TabList, Tab, TabPanels, TabPanel, ProgressSpinner, Tag],
  providers: [ProjectStore],
  template: `
    @if (projectStore.loading()) {
      <div class="loading-container">
        <p-progress-spinner />
      </div>
    } @else if (project(); as project) {
      <div class="project-detail-header">
        <div class="header-left">
          <a routerLink="/app/projects">
            <p-button icon="pi pi-arrow-left" [text]="true" />
          </a>
          <h1>{{ project.title }}</h1>
          <span class="status-badge" [class]="project.status.toLowerCase()">
            {{ project.status }}
          </span>
        </div>
        @if (projectStore.canModifyProject()) {
          <div class="header-actions">
            <p-button label="Edit" icon="pi pi-pencil" [outlined]="true" />
            <p-button label="Mark Ready" icon="pi pi-check" />
          </div>
        }
      </div>

      <div class="project-content">
        <p-card>
          <h2>{{ project.title }}</h2>
          <p>{{ project.description }}</p>

          <div class="project-meta">
            @if (project.timeline) {
              <div class="meta-item">
                <i class="pi pi-calendar"></i>
                <span>{{ project.timeline.startDate }} - {{ project.timeline.endDate }}</span>
              </div>
            }
            @if (projectStore.totalBudget(); as budget) {
              <div class="meta-item">
                <i class="pi pi-wallet"></i>
                <span>{{ budget.amount | number }} {{ budget.currency }}</span>
              </div>
            }
          </div>
        </p-card>

        <p-tabs value="budget">
          <p-tablist>
            <p-tab value="budget">Budget ({{ project.budgetItems.length }})</p-tab>
            <p-tab value="team">Team ({{ project.participants.length }})</p-tab>
            <p-tab value="notes">Notes ({{ project.notes.length }})</p-tab>
          </p-tablist>
          <p-tabpanels>
            <p-tabpanel value="budget">
              @if (project.budgetItems.length) {
                <div class="items-list">
                  @for (item of project.budgetItems; track item.id) {
                    <p-card styleClass="item-card">
                      <div class="item-row">
                        <span class="item-description">{{ item.description }}</span>
                        <span class="item-amount">
                          {{ item.amount.amount | number }} {{ item.amount.currency }}
                        </span>
                      </div>
                    </p-card>
                  }
                </div>
              } @else {
                <p-card>
                  <div class="empty-state">
                    <i class="pi pi-wallet"></i>
                    <p>No budget items yet</p>
                    @if (projectStore.canModifyProject()) {
                      <p-button label="Add Budget Item" icon="pi pi-plus" [outlined]="true" />
                    }
                  </div>
                </p-card>
              }
            </p-tabpanel>
            <p-tabpanel value="team">
              @if (project.participants.length) {
                <div class="items-list">
                  @for (participant of project.participants; track participant.id) {
                    <p-card styleClass="item-card">
                      <div class="item-row">
                        <div class="participant-info">
                          <span class="participant-name">{{ participant.name }}</span>
                          <span class="participant-email">{{ participant.email }}</span>
                        </div>
                        <p-tag
                          [value]="participant.status"
                          [severity]="getParticipantSeverity(participant.status)"
                        />
                      </div>
                    </p-card>
                  }
                </div>
              } @else {
                <p-card>
                  <div class="empty-state">
                    <i class="pi pi-users"></i>
                    <p>No team members yet</p>
                    @if (projectStore.canModifyProject()) {
                      <p-button
                        label="Invite Participant"
                        icon="pi pi-user-plus"
                        [outlined]="true"
                      />
                    }
                  </div>
                </p-card>
              }
            </p-tabpanel>
            <p-tabpanel value="notes">
              @if (project.notes.length) {
                <div class="items-list">
                  @for (note of project.notes; track note.id) {
                    <p-card styleClass="item-card">
                      <p class="note-content">{{ note.content }}</p>
                      <small class="note-meta">{{ note.createdAt }}</small>
                    </p-card>
                  }
                </div>
              } @else {
                <p-card>
                  <div class="empty-state">
                    <i class="pi pi-file"></i>
                    <p>No notes yet</p>
                    <p-button label="Add Note" icon="pi pi-plus" [outlined]="true" />
                  </div>
                </p-card>
              }
            </p-tabpanel>
          </p-tabpanels>
        </p-tabs>
      </div>
    } @else {
      <div class="empty-state">
        <i class="pi pi-exclamation-circle"></i>
        <h3>Project not found</h3>
        <a routerLink="/app/projects">
          <p-button label="Back to Projects" icon="pi pi-arrow-left" />
        </a>
      </div>
    }
  `,
  styles: `
    .loading-container {
      display: flex;
      justify-content: center;
      padding: 4rem;
    }
    .project-detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    .header-left {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .header-left h1 {
      margin: 0;
    }
    .header-actions {
      display: flex;
      gap: 0.5rem;
    }
    .status-badge {
      padding: 0.25rem 0.75rem;
      border-radius: 1rem;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
    }
    .status-badge.planning {
      background: var(--p-blue-100);
      color: var(--p-blue-700);
    }
    .status-badge.ready {
      background: var(--p-green-100);
      color: var(--p-green-700);
    }
    .status-badge.active {
      background: var(--p-purple-100);
      color: var(--p-purple-700);
    }
    .project-content {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }
    .project-meta {
      display: flex;
      gap: 1.5rem;
      margin-top: 1rem;
      color: var(--p-text-muted-color);
    }
    .meta-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .items-list {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }
    :host ::ng-deep .item-card {
      margin: 0;
    }
    .item-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .item-description {
      font-weight: 500;
    }
    .item-amount {
      font-weight: 600;
      color: var(--p-primary-color);
    }
    .participant-info {
      display: flex;
      flex-direction: column;
    }
    .participant-name {
      font-weight: 500;
    }
    .participant-email {
      font-size: 0.875rem;
      color: var(--p-text-muted-color);
    }
    .note-content {
      margin-bottom: 0.5rem;
    }
    .note-meta {
      color: var(--p-text-muted-color);
    }
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 3rem;
      text-align: center;
    }
    .empty-state i {
      font-size: 3rem;
      color: var(--p-text-muted-color);
      margin-bottom: 1rem;
    }
    .empty-state p,
    .empty-state h3 {
      color: var(--p-text-muted-color);
      margin-bottom: 1rem;
    }
  `,
})
export class ProjectDetailComponent {
  readonly projectStore = inject(ProjectStore);
  readonly id = input.required<string>();
  readonly project = computed(() => this.projectStore.selectedProject());

  getParticipantSeverity(status: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'ACCEPTED':
        return 'success';
      case 'INVITED':
        return 'info';
      case 'DECLINED':
        return 'danger';
      default:
        return 'secondary';
    }
  }
}
