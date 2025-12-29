import {Component} from '@angular/core';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';
import {SettingsAdminComponent} from './settings.admin.component';

@Component({
  selector: 'app-settings',
  imports: [Card, FormsModule, TableModule, SettingsAdminComponent],
  template: `
    <div class="max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold mb-6">Settings</h1>

      <app-settings-admin></app-settings-admin>

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

}
