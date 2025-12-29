import {Component, inject} from '@angular/core';
import {Card} from 'primeng/card';
import {InputText} from 'primeng/inputtext';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';
import {AuthStore} from '../../../core';
import {WorkspaceConfigStore} from '../store/workspace-config.store';

@Component({
  selector: 'app-settings',
  imports: [Card, InputText, Select, FormsModule],
  template: `
    <div class="max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold mb-6">Settings</h1>

      @if (authStore.isAdmin()) {
        <p-card header="Workspace Configuration" subheader="Admin only" class="mb-4">
          <div class="flex flex-col gap-4">
            <div class="flex flex-col gap-1">
              <label for="workspaceName">Workspace Name</label>
              <input
                pInputText
                id="workspaceName"
                [ngModel]="workspaceConfigStore.config().name"
                (ngModelChange)="workspaceConfigStore.updateConfig({name: $event})"
              />
            </div>

            <div class="flex flex-col gap-1">
              <label for="logoUrl">Logo URL</label>
              <input
                pInputText
                id="logoUrl"
                [ngModel]="workspaceConfigStore.config().logoUrl"
                (ngModelChange)="workspaceConfigStore.updateConfig({logoUrl: $event})"
              />
            </div>

            <div class="flex flex-col gap-1">
              <label for="font">Font</label>
              <p-select
                id="font"
                [options]="fontOptions"
                [ngModel]="workspaceConfigStore.config().fontFamily"
                (ngModelChange)="workspaceConfigStore.updateConfig({fontFamily: $event})"
              />
            </div>
          </div>
        </p-card>
      }

      <p-card header="Profile" class="mb-4">
        <p>Manage your profile information.</p>
      </p-card>

      <p-card header="Notifications" class="mb-4">
        <p>Configure notification preferences.</p>
      </p-card>

      <p-card header="Privacy" class="mb-4">
        <p>Manage privacy settings.</p>
      </p-card>
    </div>
  `
})
export class SettingsComponent {
  protected readonly authStore = inject(AuthStore);
  protected readonly workspaceConfigStore = inject(WorkspaceConfigStore);

  fontOptions = [
    'Rubik',
    'Inter',
    'Roboto',
    'Open Sans',
    'Lato',
    'Montserrat',
    'Poppins'
  ];

}
