import {Component, inject, OnInit} from '@angular/core';
import {WorkspaceConfigStore} from '../store/workspace-config.store';
import {AuthStore, UserRole} from '../../../core';
import {UserStore} from '../store/user.store';
import {Card} from 'primeng/card';
import {InputText} from 'primeng/inputtext';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';

@Component({
  selector: 'app-settings-admin',
  imports: [Card, InputText, Select, FormsModule, TableModule],
  template: `
    @if (authStore.isAdmin()) {
      <p-card header="Workspace Configuration" class="mb-4">
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

      <p-card header="User Management" class="mb-4">
        <p-table [value]="userStore.users()" [loading]="userStore.loading()">
          <ng-template #header>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
            </tr>
          </ng-template>
          <ng-template #body let-user>
            <tr>
              <td>{{ user.name }}</td>
              <td>{{ user.email }}</td>
              <td>
                <p-select
                  [options]="roleOptions"
                  [ngModel]="user.role"
                  (ngModelChange)="userStore.updateRole({userId: user.id, role: $event})"
                  appendTo="body"
                />
              </td>
            </tr>
          </ng-template>
        </p-table>
      </p-card>
    }
  `
})
export class SettingsAdminComponent implements OnInit {
  protected readonly authStore = inject(AuthStore);
  protected readonly workspaceConfigStore = inject(WorkspaceConfigStore);
  protected readonly userStore = inject(UserStore);

  fontOptions = ['Rubik', 'Inter', 'Roboto', 'Open Sans', 'Lato', 'Montserrat', 'Poppins'];
  roleOptions: UserRole[] = ['ADMIN', 'USER'];

  ngOnInit() {
    console.log(this.authStore.selectedWorkspace()?.userRole);
    if (this.authStore.isAdmin()) {
      this.userStore.loadUsers();
    }
  }
}
