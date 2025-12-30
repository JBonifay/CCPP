import {Component, effect, inject} from '@angular/core';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {Card} from 'primeng/card';
import {Button} from 'primeng/button';
import {InputText} from 'primeng/inputtext';
import {Message} from 'primeng/message';
import {finalize} from 'rxjs';
import {AuthStore} from '../../../core';
import {AppRoutePaths} from '../../../app.routes';
import {WorkspaceRepository} from '../data/workspace.repository';

@Component({
  selector: 'app-create-workspace',
  imports: [FormsModule, Card, Button, InputText, Message],
  template: `
    <div class="create-workspace-container">
      <p-card styleClass="create-workspace-card">
        <ng-template #header>
          <div class="create-workspace-header">
            <h2>Create Workspace</h2>
            <p>Set up a new workspace</p>
          </div>
        </ng-template>

        @if (error) {
          <p-message severity="error" [text]="error" styleClass="mb-3 w-full"/>
        }

        <form (ngSubmit)="onSubmit()" class="create-workspace-form">
          <div class="field">
            <label for="name">Workspace Name</label>
            <input
              pInputText
              id="name"
              type="text"
              [(ngModel)]="name"
              name="name"
              placeholder="Enter workspace name"
              class="w-full"
              required
            />
          </div>

          <div class="field">
            <label for="logoUrl">Logo URL (optional)</label>
            <input
              pInputText
              id="logoUrl"
              type="url"
              [(ngModel)]="logoUrl"
              name="logoUrl"
              placeholder="https://example.com/logo.png"
              class="w-full"
            />
          </div>

          <div class="button-group">
            <p-button
              type="button"
              label="Cancel"
              severity="secondary"
              [outlined]="true"
              (onClick)="cancel()"
            />
            <p-button
              type="submit"
              label="Create"
              [loading]="loading"
            />
          </div>
        </form>
      </p-card>
    </div>
  `,
  styles: `
    .create-workspace-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 2rem;
      background: var(--p-surface-ground);
    }

    :host ::ng-deep .create-workspace-card {
      width: 100%;
      max-width: 480px;
    }

    .create-workspace-header {
      text-align: center;
      padding: 1.5rem 1.5rem 0;
    }

    .create-workspace-header h2 {
      margin: 0 0 0.5rem;
    }

    .create-workspace-header p {
      color: var(--p-text-muted-color);
      margin: 0;
    }

    .create-workspace-form {
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }

    .field {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .field label {
      font-weight: 500;
    }

    .button-group {
      display: flex;
      gap: 0.75rem;
      justify-content: flex-end;
    }

    .w-full {
      width: 100%;
    }

    .mb-3 {
      margin-bottom: 1rem;
    }
  `,
})
export class CreateWorkspace {
  private readonly router = inject(Router);
  private readonly authStore = inject(AuthStore);
  private readonly workspaceRepository = inject(WorkspaceRepository);

  name = '';
  logoUrl = '';
  loading = false;
  error: string | null = null;

  constructor() {
    effect(() => {
      if (this.authStore.hasWorkspaceSelected()) {
        this.router.navigate(AppRoutePaths.home());
      }
    });
  }

  onSubmit(): void {
    if (!this.name.trim()) {
      this.error = 'Workspace name is required';
      return;
    }

    this.loading = true;
    this.error = null;

    this.workspaceRepository.create({
      name: this.name.trim(),
      logoUrl: this.logoUrl.trim() || undefined,
    }).pipe(
      finalize(() => this.loading = false)
    ).subscribe({
      next: (workspace) => {
        this.authStore.selectWorkspace(workspace.id);
      },
      error: (err) => {
        this.error = err?.message ?? 'Failed to create workspace';
      }
    });
  }

  cancel(): void {
    this.router.navigate(AppRoutePaths.selectWorkspace());
  }
}
