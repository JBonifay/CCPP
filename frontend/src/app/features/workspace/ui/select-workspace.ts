import {Component, effect, inject} from '@angular/core';
import {Router} from '@angular/router';
import {Card} from 'primeng/card';
import {Message} from 'primeng/message';
import {AuthStore, Workspace} from '../../../core';
import {AppRoutePaths} from '../../../app.routes';

@Component({
  selector: 'app-select-workspace',
  imports: [Card, Message],
  template: `
    <div class="workspace-container">
      <p-card styleClass="workspace-card">
        <ng-template #header>
          <div class="workspace-header">
            <h2>Select Workspace</h2>
            <p>Choose a workspace to continue</p>
          </div>
        </ng-template>

        @if (authStore.error()) {
          <p-message severity="error" [text]="authStore.error()!" styleClass="mb-3 w-full"/>
        }

        <div class="workspace-list">
          @for (workspace of authStore.workspaces(); track workspace.id) {
            <div
              class="workspace-item"
              [class.loading]="authStore.loading()"
              (click)="selectWorkspace(workspace)"
            >
              <div class="workspace-info">
                @if (workspace.logoUrl) {
                  <img [src]="workspace.logoUrl" [alt]="workspace.name" class="workspace-logo"/>
                } @else {
                  <div class="workspace-initial">{{ workspace.name.charAt(0).toUpperCase() }}</div>
                }
                <div class="workspace-details">
                  <span class="workspace-name">{{ workspace.name }}</span>
                  <span class="workspace-role">{{ workspace.role }}</span>
                </div>
              </div>
              <i class="pi pi-chevron-right"></i>
            </div>
          } @empty {
            <p-message severity="warn" text="No workspaces available" styleClass="w-full"/>
          }
        </div>
      </p-card>
    </div>
  `,
  styles: `
    .workspace-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 2rem;
      background: var(--p-surface-ground);
    }

    :host ::ng-deep .workspace-card {
      width: 100%;
      max-width: 480px;
    }

    .workspace-header {
      text-align: center;
      padding: 1.5rem 1.5rem 0;
    }

    .workspace-header h2 {
      margin: 0 0 0.5rem;
    }

    .workspace-header p {
      color: var(--p-text-muted-color);
      margin: 0;
    }

    .workspace-list {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .workspace-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1rem;
      border: 1px solid var(--p-surface-border);
      border-radius: var(--p-border-radius);
      cursor: pointer;
      transition: all 0.2s;
    }

    .workspace-item:hover:not(.loading) {
      background: var(--p-surface-hover);
      border-color: var(--p-primary-color);
    }

    .workspace-item.loading {
      opacity: 0.6;
      pointer-events: none;
    }

    .workspace-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .workspace-logo {
      width: 40px;
      height: 40px;
      border-radius: var(--p-border-radius);
      object-fit: cover;
    }

    .workspace-initial {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--p-primary-color);
      color: var(--p-primary-contrast-color);
      border-radius: var(--p-border-radius);
      font-weight: 600;
      font-size: 1.25rem;
    }

    .workspace-details {
      display: flex;
      flex-direction: column;
    }

    .workspace-name {
      font-weight: 500;
    }

    .workspace-role {
      font-size: 0.875rem;
      color: var(--p-text-muted-color);
      text-transform: capitalize;
    }

    .w-full {
      width: 100%;
    }

    .mb-3 {
      margin-bottom: 1rem;
    }
  `,
})
export class SelectWorkspace {
  private readonly router = inject(Router);
  readonly authStore = inject(AuthStore);

  constructor() {
    effect(() => {
      if (this.authStore.hasWorkspaceSelected()) {
        this.router.navigate(AppRoutePaths.home());
      }
    });
  }

  selectWorkspace(workspace: Workspace): void {
    if (this.authStore.loading()) {
      return;
    }
    this.authStore.selectWorkspace(workspace.id);
  }
}
