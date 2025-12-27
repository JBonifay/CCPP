import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { ProgressSpinner } from 'primeng/progressspinner';
import { ProjectStore } from '../stores/project.store';

@Component({
  selector: 'app-project-list',
  imports: [RouterLink, Button, Card, ProgressSpinner],
  providers: [ProjectStore],
  template: `
    @if (projectStore.loading()) {
      <div class="loading-container">
        <p-progress-spinner />
      </div>
    } @else if (projectStore.hasProjects()) {
      <div class="project-grid">
        @for (project of projectStore.projects(); track project.id) {
          <p-card styleClass="project-card">
            <ng-template #header>
              <div class="project-card-header">
                <span class="status-badge" [class]="project.status.toLowerCase()">
                  {{ project.status }}
                </span>
              </div>
            </ng-template>
            <h3>{{ project.title }}</h3>
            @if (project.timeline) {
              <p class="project-timeline">
                <i class="pi pi-calendar"></i>
                {{ project.timeline.startDate }} - {{ project.timeline.endDate }}
              </p>
            }
            <ng-template #footer>
              <a [routerLink]="['/app/projects', project.id]">
                <p-button
                  label="View Details"
                  [text]="true"
                  icon="pi pi-arrow-right"
                  iconPos="right"
                />
              </a>
            </ng-template>
          </p-card>
        }
      </div>
    } @else {
      <div class="empty-state">
        <i class="pi pi-folder-open"></i>
        <h3>No projects yet</h3>
        <p>Create your first project to get started</p>
        <p-button label="New Project" icon="pi pi-plus" />
      </div>
    }
  `,
  styles: `
    .project-list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    .project-list-header h1 {
      margin: 0;
    }
    .project-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    :host ::ng-deep .project-card {
      height: 100%;
    }
    .project-card-header {
      padding: 1rem;
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
    .status-badge.cancelled,
    .status-badge.failed {
      background: var(--p-red-100);
      color: var(--p-red-700);
    }
    .project-timeline {
      color: var(--p-text-muted-color);
      font-size: 0.875rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .loading-container {
      display: flex;
      justify-content: center;
      padding: 3rem;
    }
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 4rem;
      text-align: center;
    }
    .empty-state i {
      font-size: 4rem;
      color: var(--p-text-muted-color);
      margin-bottom: 1rem;
    }
    .empty-state h3 {
      margin-bottom: 0.5rem;
    }
    .empty-state p {
      color: var(--p-text-muted-color);
      margin-bottom: 1.5rem;
    }
  `,
})
export class ProjectListComponent {
  readonly projectStore = inject(ProjectStore);
}
