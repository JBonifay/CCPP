import {Component} from '@angular/core';
import {Card} from 'primeng/card';

@Component({
  selector: 'app-settings',
  imports: [Card],
  template: `
    <div class="max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold mb-6">Settings</h1>

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
